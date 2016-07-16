<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${recipeTitle}</title>
</head>
<body bgcolor="#FFFFFF">
<table width="100%" border="0" bgcolor="#e6e6e6">
<tr>
    <td>
    	<table width="614" border="0" align="center" cellpadding="0" cellspacing="0">
			<tr>
        		<td width="1" height="42"><img src="cid:cleargif" style="display: block" width="1" height="42"/></td>
	        	<td width="613"><font face="Arial, Helvetica, sans-serif" color="#666666" style="font-size: 11px; line-height: 42px"></font></td>
        	</tr>
    	</table>
		<table width="614" border="0" align="center" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF">
      		<tr>
    			<td>
					<table width="614" height="70" border="0" cellspacing="0" cellpadding="0">
      					<tr>
        					<td width="18" height="21"><img src="cid:cleargif" style="display: block" width="18" height="21" border="0"/></td>
				            <td></td>
				            <td></td>
				            <td></td>
						</tr>
						<tr>
				            <td width="18" height="56"></td>
				            <td width="153"><a href=${recipeOrganizerUrl} target="_blank"><img src="cid:rologo" alt="RecipeOrganizer" border="0" align="left" height="56" width="153" style="display: block"/></a></td>
				            <td width="415" align="right" valign="middle">
				            	<i><font face="Arial, Helvetica, sans-serif" color="#855247" style="font-size: 18px; line-height: 55px;">${tagline}</font></i>
				            	<sup style="font-family: Arial, Helvetica, san-serif; color:#855247">&nbsp;&copy;</sup>
				            </td>
				            <td width="28"></td>
						</tr>
						<tr>
							<td width="18" height="11"><img src="cid:cleargif" style="display: block" width="18" height="11" border="0" /></td>
							<td></td>
							<td></td>
						</tr>
					</table>
					<table width="614" border="0" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF">
						<tr>
							<td width="16" height="28"><img src="cid:cleargif" border="0" style="display:block" width="16" height="53"/></td>
							<td width="11" bgcolor="#7e8e82"></td>
							<td width="558" align="left" valign="middle" bgcolor="#7e8e82">
								<font face="Arial, Helvetica, sans-serif" color="#ffffff" style="font-size: 18px; line-height: 55px;">${recipeTitle}</font></td>
							<td width="11" align="right" valign="top" bgcolor="#7e8e82">&nbsp;</td>
							<td width="18" align="right" valign="top"><img src="cid:cleargif" border="0" style="display:block" width="16" height="20"/></td>
						</tr>
					</table>
					<table width="614" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="18" height="24"><img src="cid:cleargif" style="display: block" width="18" height="24"/></td>
							<td width="538"><img src="cid:cleargif" style="display: block" width="538" height="24"/></td>
							<td width="40"><img src="cid:cleargif" style="display: block" width="40" height="24"/></td>
							<td width="18"><img src="cid:cleargif" style="display: block" width="18" height="24"/></td>
						</tr>
						<tr>
							<td></td>
							<td><font face="Arial, Helvetica, sans-serif" color="#855247" style="font-size: 16px; line-height: 19px">${dearUser},</font>
							<br><br>
							<font face="Arial, Helvetica, sans-serif" color="#333333" style="font-size: 13px; line-height: 19px">
							<strong>${senderName}</strong>&nbsp;${senderShare}
							<br><br>
							</font>
							<table width="490" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td width="20" valign="top"><img src="cid:cleargif" style="display:block" width="20" height="5"/></td>
									<td width="400" valign="top">
										<font face="Arial, Helvetica, sans-serif" color="#855247" style="font-size: 13px; line-height: 19px"><strong>${recipeName}</strong></font>
									</td>
									<td width="70" valign="top"><img src="cid:cleargif" style="display:block" width="70" height="5"/></td>
								</tr>
							</table>
							<font face="Arial, Helvetica, sans-serif" color="#333333" style="font-size: 13px; line-height: 19px">
							<#if userMessage?length gt 0>
								<br>
								${noteLabel}
								<br><br>
								<table width="490" border="0" cellpadding="0" cellspacing="0">
									<tr>
										<td width="20" valign="top"><img src="cid:cleargif" style="display:block" width="20" height="5"/></td>
										<td width="400" valign="top">
											<font face="Arial, Helvetica, sans-serif" color="#855247" style="font-size: 13px; line-height: 19px">
											<strong><em>${userMessage}</em></strong>
										</td>
										<td width="70" valign="top"><img src="cid:cleargif" style="display:block" width="70" height="5"/></td>
									</tr>
								</table>
							</#if>
							<br><br>
							${problems}
							<br><br>
							${signup}&nbsp;<a href=${recipeOrganizerUrl} style="text-decoration: none">www.recipeorganizer.net.</a>&nbsp;${nocost}
							<br><br><br>
							</font>
							<font face="Arial, Helvetica, sans-serif" color="#855247" style="font-size: 16px; line-height: 19px">${folks}</font>							
							</td>
							<td></td>
							<td></td>
						</tr>
						<tr>
							<td width="18" height="5"><img src="cid:cleargif" style="display: block" width="18" height="60"/></td>
							<td></td>
						</tr>
						<tr>
							<td height="1"></td>
							<td width="578" height="1" colspan="2" bgcolor="#999999" background="cid:cleargif"></td>
							<td height="1"></td>
						</tr>
					</table>
					<table width="614" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="18" height="10"><img src="cid:cleargif" style="display: block" width="18" height="13"/></td>
							<td width="503" height="10"><img src="cid:cleargif" style="display: block" width="503" height="13"/></td>
							<td width="75"><img src="cid:cleargif" style="display: block" width="75" height="1"/></td>
							<td width="18" height="10"><img src="cid:cleargif" style="display: block" width="18" height="13"/></td>
						</tr>
						<tr>
							<td></td>
							<td><font face="Arial, Helvetica, sans-serif" color="#333333" style="font-size: 13px; line-height: 19px">
								&copy; ${copyright}
								<br>
								2607 Thayer | Evanston, IL 60201 | <a href=${recipeOrganizerUrl} style="text-decoration: none">www.recipeorganizer.net</a>
							</font></td>
							<td></td>
						</tr>
						<tr>
							<td width="18" height="10"><img src="cid:cleargif" style="display: block" width="18" height="13"/></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		<table width="614" border="0" align="center" cellpadding="0" cellspacing="0">
			<tr>
				<td width="18" height="42"><img src="cid:cleargif" style="display: block" width="18" height="42"/></td>
			</tr>
		</table>
	</td>
</tr>
</table>
</body>
</html>
