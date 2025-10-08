import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.navigation.ROUT_HOME
import com.navigation.ROUT_REGISTER
import com.triple7.healthshield254.R
import com.triple7.healthshield254.ui.screens.auth.AuthViewModel
import com.triple7.healthshield254.ui.theme.tripleSeven

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tripleSeven)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.width(20.dp))

        Image(
            painter = painterResource(id = R.drawable.medicalinsurance),
            contentDescription = "sikiafiti",
            modifier = Modifier.size(width = 200.dp, height = 250.dp).clip(shape = RoundedCornerShape(10.dp)),
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "HealhShield",
            fontSize = 40.sp,
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(30.dp))

        Text(
            text = "Welcome back",
            color = Color.White

        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f) // 85% of screen width
                .height(400.dp), // adjust height as needed
            shape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp, bottomStart = 60.dp, bottomEnd = 60.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//Email
                OutlinedTextField(
                    value = email,
                    onValueChange = {email = it},
                    modifier = Modifier.width(360.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = tripleSeven,
                        unfocusedLeadingIconColor = tripleSeven
                    ),
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") },
                    label = {Text(text = "Email")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                //End of Email
                Spacer(modifier = Modifier.height(10.dp))

                //Password
                OutlinedTextField(
                    value = password,
                    onValueChange = {password = it},
                    modifier = Modifier.width(360.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = tripleSeven,
                        unfocusedLeadingIconColor = tripleSeven
                    ),
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "lock") },
                    label = {Text(text = "Password")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation()
                )
                //End of Password

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = {navController.navigate(ROUT_REGISTER)} ) {
                    Text(
                        text = "Don't have an account? Sign Up"
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                val context = LocalContext.current
                val authViewModel = AuthViewModel(navController, context)

                TextButton(onClick = {
                    navController.navigate(ROUT_HOME)
                }) {
                    Text(
                        text = "Login",
                        color = tripleSeven

                    )

                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LoginScreenPreview(){
    LoginScreen(rememberNavController())
}
