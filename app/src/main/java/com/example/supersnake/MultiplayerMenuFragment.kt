package com.example.supersnake

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [MultiplayerMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MultiplayerMenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var txtUsername: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_multiplayerMenuFragment_to_menuFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_multiplayer_menu, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MultiplayerMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MultiplayerMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchingAnimation: LottieAnimationView = view.findViewById(R.id.animationSearching)
        searchingAnimation.visibility = View.GONE

        val btnPlayerSearch: Button = view.findViewById(R.id.btnSearchPlayer)
        btnPlayerSearch.setOnClickListener(){

            txtUsername = view.findViewById(R.id.txtUsername)
            if(txtUsername.text.toString().isEmpty()){
                //edit Text is empty
                showToast("Please enter your username")

            }else{
                val newGame = getString(R.string.NEW_GAME)
                SocketHandler.emit(newGame, txtUsername.text.toString().trim())
                Log.d("txtUsername", "Username: ${txtUsername.text.toString()}")
                btnPlayerSearch.visibility = View.GONE
                searchingAnimation.visibility = View.VISIBLE
            }


        }

        val btnToMultiplayerScreen: Button = view.findViewById<Button>(R.id.btnTestMulti)
        btnToMultiplayerScreen.setOnClickListener(){

            findNavController().navigate(R.id.multiplayerGameFragment)
        }

        val connect = getString(R.string.connect)//get the Event Name from string
        SocketHandler.setSocket();
        SocketHandler.establishConnection()
        val mSocket = SocketHandler.getSocket()//get the Socket

        SocketHandler.on(connect) { args ->
            Log.d("Socket", "You connected with id: ${mSocket.id()}")
        }


    }






    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }









}