package com.example.supersnake

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import io.socket.emitter.Emitter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//controls

@SuppressLint("StaticFieldLeak")
private lateinit var buttonUp : ImageView
@SuppressLint("StaticFieldLeak")
private lateinit var buttonDown : ImageView
@SuppressLint("StaticFieldLeak")
private lateinit var buttonLeft : ImageView
@SuppressLint("StaticFieldLeak")
private lateinit var buttonRight : ImageView

private lateinit var movementEvent: String
private lateinit var handleGameStateEvent: String
private lateinit var gameState: GameState

private lateinit var snakeMultiplayerView: SnakeMultiplayerView





/**
 * A simple [Fragment] subclass.
 * Use the [MultiplayerGameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MultiplayerGameFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    lateinit var mp: MediaPlayer
    lateinit var player1TextView: TextView
    lateinit var player2TextView: TextView

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
        val view = inflater.inflate(R.layout.fragment_multiplayer_game, container, false)
        //surfaceView = view.findViewById(R.id.surfaceViewMultiplayer)
        //surfaceView.holder.addCallback(this)
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MultiplayerGameFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MultiplayerGameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snakeMultiplayerView = view.findViewById(R.id.snakeMultiplayerView)
        player1TextView = view.findViewById(R.id.txtPlayer1)
        player2TextView = view.findViewById(R.id.txtPlayer2)
        mp = MediaPlayer.create(context, R.raw.epic_dramatic)
        mp.isLooping = true
        mp.start()
        init(view)

        println("OnViewCreated")

        buttonLeft.setOnClickListener {
            //Handle Left
            SocketHandler.emit(movementEvent, 37)
            Log.d("Socket", "Button_Left: ${37}")
        }

        buttonDown.setOnClickListener {
            //Handle Down
            SocketHandler.emit(movementEvent, 38)
            Log.d("Socket", "Button_Down: ${38}")
        }

        buttonRight.setOnClickListener {
            //Handle Right
            SocketHandler.emit(movementEvent, 39)
            Log.d("Socket", "Button_Right: ${39}")
        }

        buttonUp.setOnClickListener {
            //Handle Up
            SocketHandler.emit(movementEvent, 40)
            Log.d("Socket", "Button_Up: ${40}")
        }

        SocketHandler.on(handleGameStateEvent) { args ->
            val gson = Gson()
             gameState = gson.fromJson(args[0] as String, GameState::class.java)
            println(gameState)
            player1TextView.text = "Player 1: " + gameState.players[0].playerName
            player2TextView.text = "Player 2: " + gameState.players[1].playerName
            activity?.runOnUiThread {
                println("After")
                println(gameState)
                drawUpdate(gameState)
            }

        }

    }

    private fun drawUpdate(gameState: GameState){
        //update stuff
        snakeMultiplayerView.setStuff(gameState);


    }

    override fun onPause() {
        super.onPause()
        mp.stop()
        mp.release()
    }


    private fun init(view: View){
        buttonUp = view.findViewById(R.id.arrowUp)
        buttonDown = view.findViewById(R.id.arrowDown)
        buttonLeft = view.findViewById(R.id.arrowLeft)
        buttonRight = view.findViewById(R.id.arrowRight)

        movementEvent = getString(R.string.MOVEMENT)
        handleGameStateEvent = getString(R.string.UPDATE_GAME_STATE)
    }

}

