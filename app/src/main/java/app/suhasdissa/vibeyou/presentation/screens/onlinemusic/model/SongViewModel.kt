package app.suhasdissa.vibeyou.presentation.screens.onlinemusic.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.suhasdissa.vibeyou.MellowMusicApplication
import app.suhasdissa.vibeyou.backend.repository.SongDatabaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class SongViewModel(private val songDatabaseRepository: SongDatabaseRepository) : ViewModel() {
    val songs = songDatabaseRepository.getAllSongsStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = listOf()
    )

    val favSongs = songDatabaseRepository.getFavSongsStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = listOf()
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MellowMusicApplication)
                SongViewModel(
                    application.container.songDatabaseRepository
                )
            }
        }
    }
}

