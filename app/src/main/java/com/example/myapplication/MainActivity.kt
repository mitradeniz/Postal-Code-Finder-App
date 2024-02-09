package com.example.myapplication

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.net.InetSocketAddress
import java.net.Socket
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.font.FontWeight


internal var l: List<String> = mutableStateListOf()
internal var a: String = ""
internal var b: String = ""
internal var c: String = ""
internal var d: String = ""

class MainActivity : ComponentActivity() {
    fun changePage(){
        for (i in 1..3){
            l = Query(a,b,c,d)

        }
        val navigate = Intent(this@MainActivity, ListPageActivity::class.java)
        startActivity(navigate)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var il by remember { mutableStateOf("") }
                    var ilce by remember { mutableStateOf("") }
                    var semtBucakMahalle by remember { mutableStateOf("") }
                    var mahalle by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),

                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly,

                        ) {
                        TextField(
                            value = il,
                            onValueChange = { newIl -> il = newIl },
                            label = { Text("İl") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        TextField(
                            value = ilce,
                            onValueChange = { newIlce -> ilce = newIlce },
                            label = { Text("Ilçe") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        TextField(
                            value = semtBucakMahalle,
                            onValueChange = { newSemtBucakMahalle -> semtBucakMahalle = newSemtBucakMahalle},
                            label = { Text(text = "Semt")},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        TextField(
                            value = mahalle,
                            onValueChange = { newMahalle -> mahalle = newMahalle },
                            label = { Text("Mahalle") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        Button(
                            onClick = {
                                /*
                                val resultList = Query(il, ilce, semtBucakMahalle, mahalle)
                                CoroutineScope(Dispatchers.IO).launch {
                                    val resultList = Query(il, ilce, semtBucakMahalle, mahalle)
                                    for (i in 0..1300000000){

                                    }
                                    l = resultList
                                    for (i in 0..1300000000){

                                    }
                                }
                                startActivity(navigate)

                                 */
                                a = il
                                b = ilce
                                c = semtBucakMahalle
                                d = mahalle

                                l = Query(a, b, c, d)

                                for (i in 0..100000000){
                                    for (a in 0..10){

                                    }
                                }

                                changePage()

                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ARA")
                        }



                    }
                }
            }
        }
    }

}

@Composable
fun reloadList() {
    ListViewPage(l)
}

internal fun Query(il: String, ilce: String, semtBucakMahalle: String, mahalle: String): List<String> {

    val query = "${il.toUpperCase()},${ilce.toUpperCase()},${semtBucakMahalle.toUpperCase()},${mahalle.toUpperCase()}"
    val serverIp = "10.40.125.17"
    val serverPort = 12345
    val serverAddress = InetSocketAddress(serverIp, serverPort)
    var serverResponse: String

    ServerTask(object : ServerTask.OnServerTaskListener {
        override fun onTaskComplete(result: String) {
            serverResponse = result
            Log.e("serverResponse", result)
            l = responseDataProcess(serverResponse)
        }
    }).execute(serverAddress, query)

    return l
}

@Composable
fun ListViewPage(l: List<String>) {
    LazyColumn() {

        items(l) { infos ->
            CardCont(infos = infos)
            Divider()
        }
    }
}

@Composable
fun CardCont(infos: String) {
    Card(colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primary
    ), modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)) {
        Composition(infos = infos)

    }
}

@Composable
fun Composition(infos: String) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Row (modifier = Modifier
        .background(Color(R.color.cardBackground))
        .padding(12.dp)
        .animateContentSize()) {

        Column(
            Modifier
                .weight(1f)
                .padding(12.dp)) {
            Text(text = "Bilgiler")
            Text(
                text = infos,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            if (expanded) {
                Text(text = "---")
            }


        }

    }
}


class ServerTask(private val listener: OnServerTaskListener) : AsyncTask<Any, Void, String>() {
    interface OnServerTaskListener {
        fun onTaskComplete(result: String)
    }

    override fun doInBackground(vararg params: Any): String {
        val serverAddress = params[0] as InetSocketAddress
        val query = params[1] as String

        var response = ""

        try {
            val clientSocket = Socket()
            clientSocket.connect(serverAddress)

            val outputStream = clientSocket.getOutputStream()
            val inputStream = clientSocket.getInputStream()

            outputStream.write(query.toByteArray())
            val buffer = ByteArray(124444)
            val bytesRead = inputStream.read(buffer)
            if (bytesRead != -1) {
                response = buffer.decodeToString(0, bytesRead)
            }

            clientSocket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return response
    }

    override fun onPostExecute(result: String) {
        listener.onTaskComplete(result)
    }
}

private fun responseDataProcess(response: String): List<String> {
    var a = ""
    val list = mutableListOf<String>()
    if (response.length <= 50) {
        list.add(response)
        return list
    }

    for (i in response) {
        a += i
        if (i == '\n') {
            list.add(a)
            a = ""
            continue
        }

    }
    list.add(a)
    return list
}