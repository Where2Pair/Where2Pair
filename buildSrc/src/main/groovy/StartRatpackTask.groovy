import org.gradle.api.*
import org.gradle.api.tasks.*
import java.util.concurrent.*
import groovyx.net.http.*

class StartRatpackTask extends DefaultTask {
	def runAppTask
	def port = '5050'
	
	@TaskAction
	def startApp() {
		def executor = Executors.newSingleThreadExecutor()
		executor.submit({ runAppTask.execute() })
		waitForAppToStart()
	}
	
	def waitForAppToStart() {
		def ratpackRunning = false
		def connectCounter = 0
		while (!ratpackRunning) {
			if (connectCounter++ == 10)
				throw new GradleScriptException("Timed-out waiting for application to start.", null)
			
			try {
				def response = new RESTClient("http://localhost:$port/").get(path: 'venues')
				ratpackRunning = response.status == 200
			} catch (Exception e) {
				Thread.sleep(500)
			}
		}
	}
}