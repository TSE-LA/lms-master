<center>
  <table class='body-wrap'
         style='text-align:center;width:86%;font-family:arial,sans-serif;border:12px solid rgba(126, 122, 122, 0.08);border-spacing:4px 20px;'>
    <tr>
      <td>
        <center>
          <table bgcolor='#FFFFFF' width='80%'
          border='0'>
          <tbody>
          <tr>
            <td style='font-family:Roboto-Regular,Helvetica,Arial,sans-serif;font-size:13px;color:#202020;line-height:1.5'>
              <h2 style='color:#575252;text-align:center;'>Сайн байна уу?</h2>
            </td>
          </tr>
          <tr style='color:#a2a2a2;height:45px;text-align:center;'>
            <td style="font-size:16px; text-align:center;">
                <#if memo??>
                  <span>Тэмдэглэл: ${memo}</span>
                </#if>
            </td>
          </tr>
          <tr style='color:#a2a2a2;height:45px;text-align:center;'>
            <td style="font-size:16px; text-align:center;">
              <span><b style='color:#575252;'>“${keyWord} ${courseName}”</b> нэртэй <b style='color:#575252;'>${startDate}-${endDate}</b> өдрийн хооронд хэрэгжих <b style='color:#575252;'>${code}</b> кодтой урамшуулал шинээр нийтлэгдлээ!</span>
            </td>
          </tr>
          <tr style='color:#a2a2a2;height:45px;text-align:center;'>
            <td style="font-size:16px; text-align:center;">
              <span>Та тус урамшууллыг эхлэхээс өмнө танилцана уу.</span>
            </td>
          </tr>
          <tr style='color:#a2a2a2;font-size:16px;height:45px;text-align:center;'>
            <td>
              <span>Нийтэлсэн админ: ${authorId}</span>
            </td>
          </tr>
          <tr style='color:#a2a2a2;font-size:16px;height:45px;text-align:center;'>
            <td>
              <a style="border-radius: 4px;display: inline-block;font-size: 14px;font-weight: bold;line-height: 24px;padding: 12px 24px;text-align: center;text-decoration: none !important;transition: opacity 0.1s ease-in;color: #ffffff !important;background-color: #66ca00;font-family: Roboto, sans-serif;"
                 href="${domainName}/#/timed-course/launch/${courseId}/true">ТАНИЛЦАХ</a>
            </td>
          </tr>
          </tbody>
  </table>
</center>
</td>
</tr>
</table></center>
