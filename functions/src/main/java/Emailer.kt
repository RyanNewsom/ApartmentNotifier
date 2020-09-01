import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import java.util.logging.Logger

interface Emailer {
    fun sendEmail(message: String, subject: String) {}
}

class EmailerImpl(
        private val logger: Logger = Logger.getLogger(Notifier::class.java.name)
) : Emailer {
    override fun sendEmail(message: String, subject: String) {
        logger.info("Sending email for subject: $subject")
        val from = Email("someEmail@gmail.com")
        val to = Email("someToEmail@gmail.com")
        val content = Content("text/plain", message)
        val mail = Mail(from, subject, to, content)

        val sg = SendGrid(API_KEY)
        val request = Request()
        request.method = Method.POST
        request.endpoint = "mail/send"
        request.body = mail.build()
        val response = sg.api(request)
        logger.info("Email Response: ${response.statusCode}\n${response.body}")
    }

    companion object {
        private const val API_KEY = "YOUR_KEY"
    }
}