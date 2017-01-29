<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>${newMessage}</title>
</head>
<body bgcolor="#FFFFFF">
<table width="100%"  cellpadding="0" cellspacing="0" border="0" bgcolor="#e6e6e6">
<tbody>
<tr>
    <td valign="top" align="left">
		<table width="100%" border="0" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF">
			<tbody>
      		<tr>
      			<td width="2%"></td>
	            <td width="20%" align="left" valign="middle"><a href=${recipeOrganizerUrl} target="_blank"><img src="cid:rologo" alt="RecipeOrganizer" border="0" align="left" height="56" width="153" style="display: block"/></a></td>
	            <td align="right" valign="middle">
	            	<i><font face="Arial, Helvetica, sans-serif" color="#855247" style="font-size: 18px;">${tagline}</font></i>
	            	<sup style="font-family: Arial, Helvetica, san-serif; color:#855247">&nbsp;&copy;</sup>
	            </td>
	            <td width="2%"></td>
      		</tr>
			<tr>
				<td width="2%" height="10" bgcolor="#7e8e82"></td>
				<td width="20%" bgcolor="#7e8e82"></td>
				<td bgcolor="#7e8e82"></td>
				<td width="2%" bgcolor="#7e8e82"></td>
			</tr>
			<tbody>
      	</table>
	</td>
</tr>
<tr>
    <td vAlign="top" align="left">
		<table width="100%" border="0" cellpadding="0" cellspacing="0" bgcolor="#FFFFFF">
      		<tr>
    			<td>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="2%" height="10"></td>
							<td></td>
							<td width="2%"></td>
						</tr>
						<tr>
							<td width="2%"></td>
							<td><font face="Arial, Helvetica, sans-serif" color="#333333" style="font-size: 13px; line-height: 19px">${userMessage}</font></td>
							<td width="2%"></td>
						</tr>
						<#if originalEmail?length gt 0>
							<tr>
								<td width="2%" height="10"><img src="cid:cleargif" style="display: block" width="18" height="10"/></td>
								<td height="10"><img src="cid:cleargif" style="display: block" height="10"/></td>
								<td width="2%" height="10"><img src="cid:cleargif" style="display: block" width="18" height="10"/></td>
							</tr>
							<tr>
								<td width="2%"></td>
								<td height="1" bgcolor="#999999" background="cid:cleargif"></td>
								<td width="2%"></td>
							</tr>
							<tr>
								<td width="2%" height="10"><img src="cid:cleargif" style="display: block" width="18" height="10"/></td>
								<td height="10"><img src="cid:cleargif" style="display: block" height="10"/></td>
								<td width="2%" height="10"><img src="cid:cleargif" style="display: block" width="18" height="10"/></td>
							</tr>
							<tr>
								<td width="2%"></td>
								<td>${originalEmail}</td>
								<td width="2%"></td>
							</tr>
						</#if>							
					</table>
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td width="2%" height="10"><img src="cid:cleargif" style="display: block" width="18" height="10"/></td>
							<td height="10"><img src="cid:cleargif" style="display: block" height="10"/></td>
							<td width="2%" height="10"><img src="cid:cleargif" style="display: block" width="18" height="10"/></td>
						</tr>
						<tr>
							<td width="2%"></td>
							<td height="1" bgcolor="#999999" background="cid:cleargif"></td>
							<td width="2%"></td>
						</tr>
						<tr>
							<td width="2%" height="10"><img src="cid:cleargif" style="display: block" width="18" height="10"/></td>
							<td height="10"><img src="cid:cleargif" style="display: block" height="10"/></td>
							<td width="2%" height="10"><img src="cid:cleargif" style="display: block" width="18" height="10"/></td>
						</tr>
						<tr>
							<td height="10" bgcolor="#f1e9da"></td>
							<td bgcolor="#f1e9da"></td>
							<td bgcolor="#f1e9da"></td>
						</tr>
						<tr>
							<td bgcolor="#f1e9da"></td>
							<td bgcolor="#f1e9da"><font face="Arial, Helvetica, sans-serif" color="#333333" style="font-size: 9px; line-height: 13px">
								&copy; ${copyright}
								<br>
								2607 Thayer | Evanston, IL 60201 | <a href=${recipeOrganizerUrl} style="text-decoration: none">www.recipeorganizer.net</a>
							</font></td>
							<td bgcolor="#f1e9da"></td>
						</tr>
						<tr>
							<td height="10" bgcolor="#f1e9da"></td>
							<td bgcolor="#f1e9da"></td>
							<td bgcolor="#f1e9da"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</td>
</tr>
</tbody>
</table>
</body>
</html>
