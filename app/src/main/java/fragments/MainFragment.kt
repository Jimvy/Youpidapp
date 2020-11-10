package fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import data.Sound
import adapter.SoundAdapter
import android.animation.*
import android.content.res.Configuration
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import lufra.youpidapp.MainActivity
import lufra.youpidapp.R

class MainFragment: MyFragment() {
    private lateinit var context: MainActivity
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: SoundAdapter
    private lateinit var viewLayoutManager: GridLayoutManager
    private var viewColumnCount = 1
    override var TAG: String = "=====MAINFRAGMENT====="

    private val soundClickedListener = object: SoundAdapter.SoundClickedListener {
        override fun onSoundClicked(soundViewHolder: SoundAdapter.SoundViewHolder) {
            val sound = soundViewHolder.sound!!
            val soundName = sound.name
            val duration = context.discotheque.play(soundName)
            playWtfAnimator(sound, duration.toLong())
            Log.d(TAG, "Clicked, $soundViewHolder")
        }
    }
    private val animCleanup = object: SoundAdapter.CleanupAnimationListener {
        override fun onUnbind(soundViewHolder: SoundAdapter.SoundViewHolder) {
            soundViewHolder.button.setBackgroundColor(resources.getColor(R.color.colorPrimary, null))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        context = activity as MainActivity
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        // Not sure if the following should be put in onCreateView or in onActivityCreated
        viewColumnCount = resources.getInteger(R.integer.grid_column_count)
        viewLayoutManager = GridLayoutManager(view.context, viewColumnCount)
        viewAdapter = SoundAdapter(Sound.ALL_SOUNDS_STR.mapIndexed { index, s ->
            Sound(index, s)
        }, soundClickedListener)
        recyclerView = view.findViewById<RecyclerView>(R.id.sound_recyclerview).apply {
            layoutManager = viewLayoutManager
            adapter = viewAdapter
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val randomButton = context.findViewById<Button>(R.id.button_random)
        randomButton.setOnClickListener { button ->
            val duration = context.discotheque.playRandom()
            playTargetedAnimator(button, duration.toLong())
        }
        context.setMenu("home")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "config changed: $newConfig")
        viewColumnCount = resources.getInteger(R.integer.grid_column_count)
        viewLayoutManager.spanCount = viewColumnCount
    }

    private fun playWtfAnimator(sound: Sound, duration: Long) {
        // Actually it is an ObjectAnimator, but we will not specify its target
        val animator = AnimatorInflater.loadAnimator(context, R.animator.fade) as ValueAnimator
        animator.addUpdateListener { va ->
            val animVal = va?.animatedValue as Int
            val viewHolder = viewAdapter.getActiveViewHolder(sound)
            viewHolder?.button?.setBackgroundColor(animVal)
            viewHolder?.cleanupAnimationListener = animCleanup
        }
        animator.duration = duration
        animator.start()
    }

    private fun playTargetedAnimator(view: View, duration: Long) {
        val animator = AnimatorInflater.loadAnimator(context, R.animator.fade)
        animator.setTarget(view)
        animator.duration = duration
        animator.start()
        Log.d(TAG, "$view, $animator")
    }
}
