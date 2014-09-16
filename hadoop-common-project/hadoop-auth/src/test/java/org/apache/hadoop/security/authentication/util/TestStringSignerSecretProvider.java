begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authentication.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authentication
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authentication
operator|.
name|server
operator|.
name|AuthenticationFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestStringSignerSecretProvider
specifier|public
class|class
name|TestStringSignerSecretProvider
block|{
annotation|@
name|Test
DECL|method|testGetSecrets ()
specifier|public
name|void
name|testGetSecrets
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|secretStr
init|=
literal|"secret"
decl_stmt|;
name|StringSignerSecretProvider
name|secretProvider
init|=
operator|new
name|StringSignerSecretProvider
argument_list|()
decl_stmt|;
name|Properties
name|secretProviderProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|secretProviderProps
operator|.
name|setProperty
argument_list|(
name|AuthenticationFilter
operator|.
name|SIGNATURE_SECRET
argument_list|,
literal|"secret"
argument_list|)
expr_stmt|;
name|secretProvider
operator|.
name|init
argument_list|(
name|secretProviderProps
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|byte
index|[]
name|secretBytes
init|=
name|secretStr
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|secretBytes
argument_list|,
name|secretProvider
operator|.
name|getCurrentSecret
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
index|[]
name|allSecrets
init|=
name|secretProvider
operator|.
name|getAllSecrets
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|allSecrets
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|secretBytes
argument_list|,
name|allSecrets
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

