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
		
		port(8102);
        get("/payment", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("service", "Payment");
            return new ModelAndView(model, "financialFrontPayment.vm");
        }, new VelocityTemplateEngine());

    }

}
