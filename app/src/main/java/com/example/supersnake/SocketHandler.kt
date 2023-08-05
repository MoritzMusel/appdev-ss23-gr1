package com.example.supersnake
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException


object SocketHandler {
    lateinit var mSocket: Socket

    /**
     * Sets up the socket for communication with the server.
     * This method establishes a connection to the specified server using a socket.
     * The server is accessed using the URL "https://supersnake-backend.onrender.com/".
     * Once a successful connection is established, the socket can be used for communication.
     * @throws URISyntaxException If the provided URI is invalid.
     */
    @Synchronized
    fun setSocket(){
        try{
            mSocket = IO.socket("https://supersnake-backend.onrender.com/");
            mSocket.connect()
        }catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    /**
     * Retrieves the socket used for communication with the server.
     * This method returns the socket that has been previously set up for communication.
     *
     * @return The socket instance used for communication.
     */
    @Synchronized
    fun getSocket(): Socket? {
        return mSocket
    }

    /**
     * Establishes a connection using the socket for communication.
     * This method initiates a connection using the previously configured socket
     * for communication with the server.
     */
    @Synchronized
    fun establishConnection() {
        mSocket.connect()
    }

    /**
     * Closes the connection established using the socket.
     * This method terminates the connection previously established
     * using the configured socket for communication with the server.
     */
    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }

    /**
     * Registers a callback function for a specific event.
     * This method associates the provided callback function with a specified event.
     * When the event is received from the server, the callback function will be invoked.
     *
     * @param event The name of the event to listen for.
     * @param callback The callback function to be executed when the event occurs.
     */
    @Synchronized
    fun on(event: String, callback: (Array<Any>) -> Unit) {
        mSocket.on(event, callback)
    }

    /**
     * Emits data for a specific event to the server.
     * This method sends the provided data to the server under the specified event.
     *
     * @param event The name of the event to emit data for.
     * @param data The data to be sent along with the event.
     */
    @Synchronized
    fun emit(event: String,  data: Any) {
        mSocket.emit(event, data)
    }

}