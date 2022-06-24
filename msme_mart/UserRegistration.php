<?php include_once("include/session.php");?>
<?php include_once("include/db_connect.php");?>
<?php include_once("include/functions.php");?>
<?php $token = $_SESSION["token"]; ?>
<?php  
if($token=$_POST['token']){
        
  $first_name     = test_input($_POST["first_name"]);
  $last_name      = test_input($_POST["last_name"]);
  $mobile         = test_input($_POST["mobile"]);
  $district       = test_input($_POST["district"]);
  $address        = test_input($_POST["address"]);
  $role           = test_input($_POST["role"]);

  $checkuser=check_mobile($con,$mobile);

  if($checkuser->rowCount()==0){

		$UName=strtoupper(substr($first_name, 0,3));
    $UMobile=strtoupper(substr($mobile, 6,9));
    $password=md5($UName.$UMobile);
      
  
		$register=user_registration($con,$first_name,$last_name,$mobile,$password,$district,$address,$role);
		$response =true;
    $message  ='Registered successfully';
    $data     =$role;
	}
	
	else if($checkuser->rowCount()>0){
    $response =false;
    $message  ='Mobile number already registered with us';
    $data     =null;
  }
 
  $response= array('Response'=>$response,'Message'=>$message,'Data'=>$data);
  echo json_encode($response); 

 } 

?>