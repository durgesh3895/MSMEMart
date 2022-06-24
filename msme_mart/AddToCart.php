<?php include_once("include/session.php");?>
<?php include_once("include/db_connect.php");?>
<?php include_once("include/functions.php");?>
<?php $token = $_SESSION["token"]; ?>
<?php  
if($token=$_POST['token']){
        
  $user_id       = test_input($_POST["user_id"]);
  $product_id      = test_input($_POST["product_id"]);


  $checkcart=check_cart($con,$user_id,$product_id);


  if($checkcart->rowCount()==0){
  
		$register=add_to_cart($con,$user_id,$product_id);
		$response =true;
    $message  ='Added into cart';
    $data     =$user_id;
	}
	
	else if($checkcart->rowCount()>0){
    $response =false;
    $message  ='Already added into cart';
    $data     =null;
  }
 
  $response= array('Response'=>$response,'Message'=>$message,'Data'=>$data);
  echo json_encode($response); 

 } 

?>