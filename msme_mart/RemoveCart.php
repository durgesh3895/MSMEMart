<?php include_once("include/session.php");?>
<?php include_once("include/db_connect.php");?>
<?php include_once("include/functions.php");?>
<?php $token = $_SESSION["token"]; ?>
<?php  
if($token=$_POST['token']){
        
  $user_id       = test_input($_POST["user_id"]);
  $product_id    = test_input($_POST["product_id"]);


  $register=remove_from_cart($con,$user_id,$product_id);
  $response =true;
  $message  ='Item removed';
  $data     =$user_id;
 
  $response= array('Response'=>$response,'Message'=>$message,'Data'=>$data);
  echo json_encode($response); 

 } 

?>