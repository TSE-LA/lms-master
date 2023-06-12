package mn.erin.lms.legacy.domain.unitel.service;

import java.util.Date;

/**
 * @author Bat-Erdene Tsogoo.
 */
public interface PromoExcelReportGenerator
{
  byte[] generatePromoExcelReport(Object data, String[] headers, Date startingDateRange, Date endingDateRange);
}
