package olehakaminskyi.livestreamfails.player.injection

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import olehakaminskyi.livestreamfails.player.ExoVideoPlayerController
import olehakaminskyi.livestreamfails.player.VideoPlayerController
import org.koin.dsl.bind
import org.koin.dsl.module

val KainPlayerModules = arrayOf(module {
    single { ExoPlayerFactory.newSimpleInstance(get()) } bind Player::class bind ExoPlayer::class
    single { ExoVideoPlayerController(get(), get()) as VideoPlayerController }
})