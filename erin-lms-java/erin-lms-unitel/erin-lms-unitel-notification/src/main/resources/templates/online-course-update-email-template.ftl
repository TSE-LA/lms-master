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
          <tr style='text-align:center;color:#a2a2a2;font-size:16px;height:45px;'>
              <#if previousName??>
                <td>
                  <span>Таны танилцах <b style='color:#575252;'>${previousName}</b>  сургалтын нэр <b style='color:#575252;'>${courseName}</b> нэртэй болж өөрчлөгдлөө. Хамрах хүрээ <b style='color:#575252;'>${courseType}</b> боллоо.
                  </span>
                </td>
              <#else>
                <td>
                  <span>Таны танилцах <b style='color:#575252;'>${courseName}</b> нэртэй сургалт <b style='color:#575252;'>${courseType}</b> хамрах хүрээтэй болж өөрчлөгдлөө.</span>
                </td>
              </#if>
          </tr>
          <tr style='color:#a2a2a2;font-size:16px;height:45px;text-align:center;'>
            <td>
              <span>Өөрчлөлт оруулсан админ: ${authorId}</span>
            </td>
          </tr>
          <tr style='color:#a2a2a2;font-size:16px;height:45px;text-align:center;'>
            <td>
              <a
                style="border-radius: 4px;display: inline-block;font-size: 14px;font-weight: bold;line-height: 24px;padding: 12px 24px;text-align: center;text-decoration: none !important;transition: opacity 0.1s ease-in;color: #ffffff !important;background-color: #66ca00;font-family: Roboto, sans-serif;"
                href="${domainName}/#/online-course/launch/${courseId}/true">ТАНИЛЦАХ</a>
            </td>
          </tr>
          </tbody>
  </table>
</center>
</td>
</tr>
</table></center>


