<?php 
session_start();
if (!isset($_SESSION["token"]) OR empty($_SESSION["token"])) 
{   
	$str               = "qwertyuiopmnbvcxzasdfghjkl";
	$_SESSION["token"] = $str;
}
?>