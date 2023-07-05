package app.suhasdissa.mellowmusic.backend.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import app.suhasdissa.mellowmusic.MellowMusicApplication
import app.suhasdissa.mellowmusic.backend.database.entities.Song
import app.suhasdissa.mellowmusic.backend.repository.SongRepository
import app.suhasdissa.mellowmusic.utils.addNext
import app.suhasdissa.mellowmusic.utils.asMediaItem
import app.suhasdissa.mellowmusic.utils.enqueue
import app.suhasdissa.mellowmusic.utils.forcePlay
import app.suhasdissa.mellowmusic.utils.forcePlayFromBeginning
import app.suhasdissa.mellowmusic.utils.playGracefully
import app.suhasdissa.mellowmusic.utils.playPause
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val songRepository: SongRepository,
    private val controllerFuture: ListenableFuture<MediaController>
) :
    ViewModel() {
    var controller: MediaController? by mutableStateOf(null)
        private set

    private var toBePlayed: MediaItem? = null

    init {
        controllerFuture.addListener(
            {
                Handler(Looper.getMainLooper()).post {
                    controller = controllerFuture.get()
                    toBePlayed?.let {
                        controller?.forcePlay(it)
                    }
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    fun seekNext() {
        controller!!.seekToNext()
    }

    fun seekTo(ms: Long) {
        controller!!.seekTo(ms)
    }

    fun playPause() {
        controller!!.playPause()
    }

    fun seekPrevious() {
        controller!!.seekToPrevious()
    }

    fun shuffleFavourites() {
        viewModelScope.launch {
            val shuffleQueue = songRepository.getFavSongs().shuffled().map { it.asMediaItem }
            shuffleSongs(shuffleQueue)
        }
    }

    fun shuffleAll() {
        viewModelScope.launch {
            val shuffleQueue = songRepository.getAllSongs().shuffled().map { it.asMediaItem }
            shuffleSongs(shuffleQueue)
        }
    }

    private fun shuffleSongs(shuffleQueue: List<MediaItem>) {
        viewModelScope.launch {
            controller!!.forcePlayFromBeginning(shuffleQueue)
        }
    }

    fun saveSong(song: Song) {
        viewModelScope.launch {
            songRepository.addSong(song)
        }
    }

    fun playNext(song: Song) {
        controller!!.addNext(song.asMediaItem)
    }

    fun enqueueSong(song: Song) {
        controller!!.enqueue(song.asMediaItem)
    }

    fun playSong(song: Song) {
        controller!!.playGracefully(song.asMediaItem)
    }

    fun toggleFavourite(id: String) {
        viewModelScope.launch {
            val song = songRepository.getSongById(id)
            song?.let {
                songRepository.addSong(it.toggleLike())
            }
        }
    }

    fun tryToPlayId(id: String) {
        viewModelScope.launch {
            val song: Song? = songRepository.searchSongId(id)
            song?.let {
                if (controller == null) {
                    toBePlayed = song.asMediaItem
                } else {
                    controller?.forcePlay(song.asMediaItem)
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MellowMusicApplication)
                PlayerViewModel(
                    application.container.songRepository,
                    application.container.controllerFuture
                )
            }
        }
    }
}