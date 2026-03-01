package com.riki.vojo.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.riki.vojo.R
import com.riki.vojo.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _isProUser = MutableStateFlow(false)
    val isProUser: StateFlow<Boolean> = _isProUser.asStateFlow()

    private val _selectedAvatarIndex = MutableStateFlow(0)
    val selectedAvatarIndex: StateFlow<Int> = _selectedAvatarIndex.asStateFlow()

    // Available avatars
    val proAvatars = listOf(
        AvatarItem(id = 0, name = "Avatar 1", resId = R.drawable.glad),
        AvatarItem(id = 1, name = "Avatar 2", resId = R.drawable.peter),
        AvatarItem(id = 2, name = "Avatar 3", resId = R.drawable.pizza  ),
        AvatarItem(id = 3, name = "Avatar 4", resId = R.drawable.italianflag)
    )

    val freeAvatars = listOf(
        AvatarItem(id = 0, name = "Avatar 1", resId = R.drawable.colosseum),
        AvatarItem(id = 1, name = "Avatar 2", resId = R.drawable.gladiator),
        AvatarItem(id = 2, name = "Avatar 3", resId = R.drawable.cezar),
        AvatarItem(id = 3, name = "Avatar 4", resId = R.drawable.vespa)
    )

    val availableAvatars: List<AvatarItem>
        get() = if (_isProUser.value) proAvatars else freeAvatars

    init {
        viewModelScope.launch {
            userPreferencesRepository.isProUser.collect { isPro ->
                _isProUser.value = isPro
            }
        }

        viewModelScope.launch {
            userPreferencesRepository.selectedAvatarIndex.collect { index ->
                _selectedAvatarIndex.value = index
            }
        }
    }

    fun unlockPro() {
        viewModelScope.launch {
            userPreferencesRepository.setProUser(true)
        }
    }

    fun selectAvatar(index: Int) {
        if (_isProUser.value || index == 0) {
            viewModelScope.launch {
                userPreferencesRepository.setSelectedAvatarIndex(index)
            }
        }
    }
}

// To jest Fabryka, która zastępuje Hilta i tworzy nasz ViewModel
class ProViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProViewModel(UserPreferencesRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class AvatarItem(
    val id: Int,
    val name: String,
    val resId: Int
)