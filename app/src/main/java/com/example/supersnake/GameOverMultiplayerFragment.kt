package com.example.supersnake

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"




/**
 * A simple [Fragment] subclass.
 * Use the [GameOverMultiplayerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GameOverMultiplayerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var playerNumber : Int = 0
    private lateinit var winner: String
    private lateinit var winnerText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_gameOverMultiplayerFragment_to_menuFragment)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_over_multiplayer, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GameOverMultiplayerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GameOverMultiplayerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        Log.d("Socket", "PlayerNumber: $playerNumber")
        Log.d("Socket", "Winner: $winner")

        if(playerNumber == winner.toInt()){
            winnerText.text = "I won"
        }else{
            winnerText.text = "You lose"
        }

        val btnToMenu: Button = view.findViewById<Button>(R.id.btnMultiToMenu)
        btnToMenu.setOnClickListener(){
            findNavController().navigate(R.id.action_gameOverMultiplayerFragment_to_menuFragment)
        }
    }

    private fun init(view: View){
        playerNumber = arguments?.getInt("player_number")!!
        winner = arguments?.getString("winner")!!
        winnerText = view.findViewById(R.id.txtWinner)
    }


}