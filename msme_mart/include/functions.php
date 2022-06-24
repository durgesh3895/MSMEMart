<?php require_once("session.php"); ?>
<?php

function test_input($data) {
  $data = trim($data);
  $data = stripslashes($data);
  $data = htmlspecialchars($data);
  return $data;
}


//------------------------------------------------------- functions----------------------------------------------------//


//function for checking user before registration
function check_mobile($con,$mobile){
 $stmt = $con->prepare("SELECT * FROM tbl_users WHERE mobile = :mobile");
 $stmt->execute(array(':mobile' => $mobile));
 return $stmt;
}

//function for user registration with
function user_registration($con,$first_name,$last_name,$mobile,$password,$district,$address,$role){
  $stmt = $con->prepare("INSERT INTO tbl_users(first_name,last_name,mobile,password,district,address,role) VALUES(?,?,?,?,?,?,?)");
  $stmt->execute(array($first_name,$last_name,$mobile,$password,$district,$address,$role));
  return $stmt;
}

//function for patient loginwith password and mobile
function user_login($con,$mobile,$password){

  $stmt = $con->prepare("SELECT * FROM  tbl_users WHERE mobile = :mobile LIMIT 1");
  $stmt->execute(array(':mobile' => $mobile));
  
  $hashpass=md5($password);

    if($stmt->rowCount()>0) {

      $row   = $stmt->fetch();
      if($hashpass==$row['password']) {  

         $user[] = array('id'=>$row['id'],
                      'first_name'=>$row['first_name'],
                      'last_name'=>$row['last_name'],
                      'mobile'=>$row['mobile'],
                      'district'=>$row['district'],
                      'address'=>$row['address'],
                      'status'=>$row['status'],
                      'role'=>$row['role']);
        return $user;
      }
      else{return 0;} 
    }
    else {return 1;}
}

//function for getting user list
function get_user_list($con) {
  $stmt = $con->prepare("SELECT * FROM tbl_users WHERE role=:role ORDER BY id desc");
  $stmt->execute(array(':role' => 'User'));
  return $stmt;
}

//function for getting unit list
function get_product_list($con,$limit) {
    $stmt = $con->prepare("SELECT * FROM tbl_product WHERE product_status=:product_status ORDER BY rand() LIMIT $limit");
    $stmt->execute(array(':product_status' => 1));
    return $stmt;
}

//function to update user profile
function update_user_profile($con,$user_id,$first_name,$last_name,$user_mobile,$password){
  $stmt =$con->prepare("UPDATE tbl_users SET first_name =:first_name,last_name=:last_name,mobile=:mobile WHERE id = :id");
  $stmt->execute(array(':id' => $user_id,':first_name' => $first_name,':last_name' => $last_name,':mobile' => $user_mobile));
  return $stmt;
}

//function for changing user password 
function change_password($con,$user_id,$old_password,$new_password){

  $hashpass=md5($old_password);
   
  $stmt = $con->prepare("SELECT * FROM tbl_users WHERE id =:id  LIMIT 1");
  $stmt->execute(array(':id' => $user_id));
  $row = $stmt->fetch(PDO::FETCH_ASSOC);

  if($stmt->rowCount()==1){

      if($hashpass== $row['password']){

        $newpass = md5($new_password);

        $stmt =$con->prepare("UPDATE tbl_users SET password =:password WHERE id = :id");
        $stmt->execute(array(':id' => $user_id,':password' => $newpass));
        return 1;
        
      }
    else {
      return 2;
    }
    
  }
}

//function for checking cart before added
function check_cart($con,$user_id,$product_id){
 $stmt = $con->prepare("SELECT * FROM tbl_cart WHERE user_id = :user_id AND product_id=:product_id");
 $stmt->execute(array(':user_id' => $user_id,':product_id' => $product_id));
 return $stmt;
}


function add_to_cart($con,$user_id,$product_id){
  $stmt = $con->prepare("INSERT INTO tbl_cart(user_id,product_id) VALUES(?,?)");
  $stmt->execute(array($user_id,$product_id));
  return $stmt;
}

//function for getting cart list
function get_cart_list($con,$user_id) {
 $stmt = $con->prepare("SELECT tbl_product.*,tbl_cart.id,tbl_cart.product_id,tbl_cart.user_id FROM tbl_product LEFT JOIN tbl_cart ON tbl_product.id = tbl_cart.product_id WHERE tbl_cart.user_id=:user_id");
 $stmt->execute(array(':user_id' => $user_id));
 return $stmt;
}

function remove_from_cart($con,$user_id,$product_id){
  $stmt = $con->prepare("DELETE FROM tbl_cart WHERE user_id=:user_id AND product_id=:product_id");
  $stmt->execute(array(':user_id' => $user_id,':product_id' => $product_id));
  return $stmt;
}