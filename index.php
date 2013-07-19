<?php
$databasehost = "127.0.0.1";
$databasename = "poi";
$databaseusername ="admin";
$databasepassword = "admin";

$con = mysql_connect($databasehost,$databaseusername,$databasepassword) or die(mysql_error());
mysql_select_db($databasename) or die(mysql_error());
$query = $_POST['q'];
$sth = mysql_query($query);

if (mysql_errno()) { 
	echo mysql_errno()."\n";
    echo mysql_error()."\n"; 
    echo $query."\n";
}
else
{
	echo "0\n";
	if ($_POST['op']=='query') {
		//echo 'suceess';
		
		$rows = array();
		while($r = mysql_fetch_assoc($sth)) {
			$rows[] = $r;
		}
		print json_encode($rows);
	} else {
		echo $sth;
	}
}
?>