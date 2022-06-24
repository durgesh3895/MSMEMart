<?php include_once("include/session.php");?>
<?php include_once("include/db_connect.php");?>
<?php include_once("include/functions.php");?>
<?php $token = $_SESSION["token"]; ?>
<?php  
if($token=isset($_POST["token"])){

  $user_id    = test_input($_POST["user_id"]);
	$old_password   = test_input($_POST["old_password"]);
	$new_password   = test_input($_POST["new_password"]);

    $change_pass     = change_password($con,$user_id,$old_password,$new_password);

    if($change_pass==1){  
                
      $response =true;
      $message  ='Password changed successfully';
      $data     =null;
    } 
    else if($change_pass==2){ 

      $response =true;
      $message  ='Sorry, old password did not matched';
      $data     =null;
   }
    else{ 

      $response =false;
      $message  ='Something went wrong';
      $data     =null;
   }

  $response= array('Response'=>$response,'Message'=>$message,'Data'=>$data);
  echo json_encode($response); 

 
}  
                  
?>