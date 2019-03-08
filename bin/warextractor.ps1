$destination = "C:\Users\ST20018615\Desktop\ship";
$WEBINF = Get-ChildItem -directory -path $destination  -Recurse | ? {$_.Name -eq "WEB-INF"}
$WEBINFLOC = $WEBINF.FullName
$JSP = Get-ChildItem -Directory -path $WEBINFLOC -Recurse | ? {$_.Name -eq "jsp_servlet"}
$JSPLOC = $JSP.FullName
$JSPmatched = Get-ChildItem -file -path $JSPLOC -recurse | ? {$_.Name -match '^__.*\.class$'}| Select-Object -ExpandProperty Name 
$count = $JSPmatched.count
$count