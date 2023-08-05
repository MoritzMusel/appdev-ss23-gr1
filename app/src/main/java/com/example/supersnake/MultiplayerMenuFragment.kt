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
import kotlinx.coroutines.DelicateCoroutinesApi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


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
    private lateinit var connectEvent: String
    private lateinit var roomNameEvent: String
    private lateinit var startGameEvent: String
    private lateinit var initEvent: String
    private var playerNumber: Int = 0
    private lateinit var roomName: String

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
            SocketHandler.closeConnection()
            Log.d("Socket", "Disconnected")
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

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        val searchingAnimation: LottieAnimationView = view.findViewById(R.id.animationSearching)
        searchingAnimation.visibility = View.GONE

        val btnPlayerSearch: Button = view.findViewById(R.id.btnSearchPlayer)
        btnPlayerSearch.setOnClickListener(){

            txtUsername = view.findViewById(R.id.txtUsername)
            if(txtUsername.text.toString().isEmpty()){
                showToast("Please enter your username")
            }else{
                val newGame = getString(R.string.NEW_GAME)
                SocketHandler.emit(newGame, txtUsername.text.toString().trim())
                Log.d("txtUsername", "Username: ${txtUsername.text}")
                btnPlayerSearch.visibility = View.GONE
                searchingAnimation.visibility = View.VISIBLE
            }

        }
        SocketHandler.setSocket();
        SocketHandler.establishConnection()


        /**
         * This code block listens for the "connectEvent" on the SocketHandler. Upon receiving the event,
         * it logs a message indicating the successful connection along with the socket's ID.
         */
        SocketHandler.on(connectEvent) { args ->
            Log.d("Socket", "You connected with id: ${SocketHandler.getSocket()?.id()}")
        }

        /**
         * This code block listens for the "roomNameEvent" on the SocketHandler. Upon receiving the event,
         * it extracts the room name from the arguments and updates the "roomName" variable.
         * Additionally, it logs the room name using the Android Log utility.
         */
        SocketHandler.on(roomNameEvent) { args ->
           roomName = args[0] as String
            Log.d("Socket", "RoomName: $roomName")
        }

        /**
         * This code block listens for the "initEvent" on the SocketHandler. Upon receiving the event,
         * it extracts the player number from the arguments and updates the "playerNumber" variable.
         */
        SocketHandler.on(initEvent) { args ->
            playerNumber = args[0] as Int
        }

        /**
         * This code block listens for the "startGameEvent" on the SocketHandler. Upon receiving the event,
         * it launches a coroutine on the main dispatcher to navigate to the MultiplayerGameFragment.
         * It also sends an integer value "player_number" as a bundle argument to the destination fragment.
         * @param playerNumber The player number to be sent as a bundle argument.
         */
        SocketHandler.on(startGameEvent) {
            GlobalScope.launch(Dispatchers.Main) {
                // navigate to MultiplayerGameFragment
                val bundle = Bundle()
                bundle.putInt("player_number", playerNumber)
                findNavController().navigate(R.id.multiplayerGameFragment, bundle)
            }
        }
    }


    /**
     * Displays a short-duration toast message.
     * This method displays a short-duration toast message with the provided message text.
     * @param message The message text to be displayed in the toast.
     */
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * This method initializes event-related string values by fetching them from resources.
     * It retrieves the event names used in the application from string resources.
     */
    private fun init(){
        connectEvent = getString(R.string.connect)//get the Event Name from string
        roomNameEvent = getString(R.string.ROOM_NAME)
        initEvent = getString(R.string.INIT)
        startGameEvent = getString(R.string.START_GAME)
    }
















}