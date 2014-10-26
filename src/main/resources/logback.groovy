import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import static ch.qos.logback.classic.Level.*

appender('CONSOLE', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n"
    }
}

appender('FILE', FileAppender) {
	file = "rmxlog.log"
	append = true
	encoder(PatternLayoutEncoder) {
		pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n"
	}
}

root(INFO, ['CONSOLE'])