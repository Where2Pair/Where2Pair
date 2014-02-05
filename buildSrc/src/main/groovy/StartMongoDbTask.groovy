import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class StartMongoDbTask extends DefaultTask {
	def port = 27017

	@TaskAction
	def startMongoDb() {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build();

        MongodStarter runtime = MongodStarter.getDefaultInstance();

        try {
            MongodExecutable mongodExecutable = runtime.prepare(mongodConfig);
            mongodExecutable.start();
        } catch (IOException e) {}
	}
}