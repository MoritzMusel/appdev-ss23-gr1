package com.example.supersnake

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.supersnake.snake.CallbackNavigation
import com.example.supersnake.snake.Direction
import com.example.supersnake.snake.SnakeThread


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SingleplayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SingleplayerFragment : Fragment(), SurfaceHolder.Callback, CallbackNavigation {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var mp: MediaPlayer

    private var snakeThread: SnakeThread? = null
    private lateinit var surfaceView: SurfaceView

    private var currentDirection = Direction.RIGHT // Default initial direction
    // Variables to store the initial touch position
    private var touchStartX = 0f
    private var touchStartY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.menuFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_singleplayer, container, false)
        surfaceView = view.findViewById(R.id.surfaceView)
        surfaceView.holder.addCallback(this)
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SingleplayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SingleplayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mp = MediaPlayer.create(context, R.raw.gaming_8bit)
        mp.isLooping = true
        mp.start()

        surfaceView.setOnTouchListener { _, event ->
            onTouch(event)
            true
        }
    }

    override fun onPause() {
        super.onPause()
        mp.stop()
        mp.release()
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        snakeThread = SnakeThread(p0, surfaceView, this, requireContext())
        snakeThread?.startThread()
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        //Not yet used
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        var retry = true
        snakeThread?.stopThread()
        while (retry) {
            try {
                snakeThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                // Retry
            }
        }
    }

    private fun onTouch(p1: MotionEvent?): Boolean {
        var snakeSize = snakeThread!!.snake.bodyParts.size
        var snakeHead = snakeThread!!.snake.getHead()
        when (p1?.action) {
            /*
            MotionEvent.ACTION_DOWN -> {
              // Handle touch down event
                touchStartX = p1.x
                touchStartY = p1.y
            }
            MotionEvent.ACTION_UP -> {
                // Handle touch up event
                val dx = p1.x - touchStartX
                val dy = p1.y - touchStartY
                if (abs(dx) > abs(dy)) {
                    // Horizontal swipe
                    if (dx > 0) {
                        // Swipe right
                        currentDirection = Direction.RIGHT
                    } else {
                        // Swipe left
                        currentDirection = Direction.LEFT
                    }
                } else {
                    // Vertical swipe
                    if (dy > 0) {
                        // Swipe down
                        currentDirection = Direction.DOWN
                    } else {
                        // Swipe up
                        currentDirection = Direction.UP
                    }
                }
            }
             */
            MotionEvent.ACTION_DOWN -> {
                val x = p1.x
                val y = p1.y
                if (x < snakeHead.x * snakeSize && currentDirection != Direction.RIGHT) {
                    currentDirection = Direction.LEFT
                    snakeThread!!.snake.setDirection(currentDirection)
                } else if (x > snakeHead.x * snakeSize + snakeSize && currentDirection != Direction.LEFT) {
                    currentDirection = Direction.RIGHT
                    snakeThread!!.snake.setDirection(currentDirection)
                } else if (y < snakeHead.y * snakeSize && currentDirection != Direction.DOWN) {
                    currentDirection = Direction.UP
                    snakeThread!!.snake.setDirection(currentDirection)
                } else if (y > snakeHead.y * snakeSize + snakeSize && currentDirection != Direction.UP) {
                    currentDirection = Direction.DOWN
                    snakeThread!!.snake.setDirection(currentDirection)
                }
            }
        }
        return true
    }

    override fun onGameOver(snakeSize: Number) {
        android.os.Handler(Looper.getMainLooper()).post {
            val bundle = bundleOf("score" to snakeSize.toString())
            findNavController().navigate(R.id.action_singleplayerFragment_to_gameOverFragment, bundle)
        }
    }
}