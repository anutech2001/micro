package indv.anurak.spark.test1;

import static spark.Spark.get;
import static spark.Spark.port;

import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;;

public class FinancialFront {
	//private final static Logger logger = LoggerFactory.getLogger(FinancialFront.class);
	//private final static String QUEUE_NAME = "payment";
	public static void main(final String[] args) {
		
		String frontPort = System.getenv("FRONT_PORT");
		final String paymentPort = System.getenv("PAYMENT_PORT");
		
		if(null != frontPort){
			port(Integer.parseInt(frontPort));
		}
        get("/payment", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("service", "Payment");
            model.put("port", paymentPort);
            return new ModelAndView(model, "financialFrontPayment.vm");
        }, new VelocityTemplateEngine());
    }

}
