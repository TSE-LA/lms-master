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
          <tr style='color:#a2a2a2;height:45px;'>
            <td style="font-size:14px">
                <#if previousName??>
            <td>
              <span>Танд ${previousName} танхимын сургалтын нэр ${courseName} болж, хамрах хүрээ ${courseType} болж өөрчлөгдсөн тухай мэдээлэл ирлээ.</span>
            </td>

              <#else>
                <td>
                  <span>Танд ${courseName} танхимын сургалт ${courseType} хамрах хүрээтэй болж өөрчлөгдсөн мэдээлэл ирлээ.</span>
                </td
              </#if>
            </td>
          </tr>
          <tr style='color:#a2a2a2;font-size:14px;height:30px;'>
            <td>
              <span><b style='color:#575252;'>Өөрчлөлт оруулсан багш:</b> ${authorId}</span>
            </td>
          </tr>
          <tr style='color:#a2a2a2;font-size:14px;height:30px;'>
            <td>
              <span>Асууж тодруулах зүйл байвал шууд удирдлагадаа хандана уу.</span>
            </td>
          </tr>
          <tr style='color:#a2a2a2;font-size:14px;height:30px;'>
            <td>
              <span>Баярлалаа.</span>
            </td>
          </tr>
          <tr style='text-align:center;color:#a2a2a2;font-size:14px;height:45px;'>
            <td>
              <a href="${domainName}/#/classroom-course/assessment/launch/${assessmentId}">ТАНИЛЦАХ</a></a></span>
            </td>
          </tr>
          </tbody>
  </table>
</center>
</td>
</tr>
</table></center>
