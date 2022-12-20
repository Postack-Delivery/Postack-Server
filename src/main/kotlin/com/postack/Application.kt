package com.postack

import com.postack.di.controllerModule
import com.postack.di.dataSourceModule
import com.postack.di.mainModule
import io.ktor.server.application.*
import io.ktor.server.engine.*
import com.postack.plugins.*
import com.postack.util.Environment
import com.postack.util.Environment.*
import com.postack.util.getProjectRoot
import io.ktor.network.tls.certificates.*
import io.ktor.network.tls.extensions.*
import io.ktor.server.jetty.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.routing.*
import kotlinx.html.A
import org.koin.ktor.plugin.Koin
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths
import java.security.KeyStore

class HttpsServer {
    companion object {
        val pass = "abcd1234"

        fun createServer(app: Application.() -> Unit): JettyApplicationEngine {
            val alias = "certificateAlias"

            val keystore = buildKeyStore {
                certificate(alias) {
                    hash = HashAlgorithm.SHA256
                    sign = SignatureAlgorithm.ECDSA
                    keySizeInBits = 256
                    password = pass
                }
            }

            val server = embeddedServer(Jetty, applicationEngineEnvironment {
                sslConnector(keystore,
                    alias,
                    { "".toCharArray() },
                    { pass.toCharArray() }) {
                    port = 8181
                    keyStorePath = keyStore.asFile.absoluteFile

                    module {
                        app()
                    }
                }
            })

            return server
        }


        private val KeyStore.asFile: File
            get() {
                val keyStoreFile = File("build/temp.jks")
                this.saveToFile(keyStoreFile, pass)
                return keyStoreFile
            }
    }
}
fun main() {

    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate("sampleAlias") {
            password = "foobar"
            domains = listOf(
                "127.0.0.1",
                "10.0.0.150",
                "localhost",
                "45.79.129.79",
                "postack.dev"
            )
        }
    }
    keyStore.saveToFile(keyStoreFile, "123456")

    val environment = applicationEngineEnvironment {
        log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = 80
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = "sampleAlias",
            keyStorePassword = { "123456".toCharArray() },
            privateKeyPassword = { "foobar".toCharArray() }) {
            port = 443
            keyStorePath = keyStoreFile
        }
        module(Application::module)
    }
//    HttpsServer.createServer(Application::module)
//        .start(wait = true)
    embeddedServer(Jetty,environment)
        .start(wait = true)
}

fun Application.module() {
    install(Koin) {
        modules(
            listOf(
                mainModule,
                dataSourceModule,
                controllerModule
            )
        )
    }
    configureHTTP()
    install(DoubleReceive) {
        cacheRawRequest = false
    }
    configureSecurity()
    configureSockets()
    configureSerialization()
    configureTemplating()
    configureMonitoring()
    configureRouting()
}
