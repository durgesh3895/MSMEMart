<?php
$databaseHost = '127.0.0.1';
$databaseName = 'msme_mart';
$databaseUsername = 'root';
$databasePassword = '';
 
try {
    $con = new PDO("mysql:host={$databaseHost};dbname={$databaseName}", $databaseUsername, $databasePassword);
    $con->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} 
catch(PDOException $e) {
  echo $e->getMessage();
}
 
?>