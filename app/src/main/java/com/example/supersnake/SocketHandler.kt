package com.example.supersnake
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException


object SocketHandler {
    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket(){
        try{
            mSocket = IO.socket("https://supersnake-backend.onrender.com/");
            mSocket.connect()
        }catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }
    @Synchronized
    fun on(event: String, callback: (Array<Any>) -> Unit) {
        mSocket.on(event, callback)
    }
    @Synchronized
    fun emit(event: String,  data: String) {
        mSocket.emit(event, data)
    }

}