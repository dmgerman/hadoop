begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|util
operator|.
name|NativeCodeLoader
import|;
end_import

begin_class
DECL|class|TestHdfsNativeCodeLoader
specifier|public
class|class
name|TestHdfsNativeCodeLoader
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestHdfsNativeCodeLoader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|requireTestJni ()
specifier|private
specifier|static
name|boolean
name|requireTestJni
parameter_list|()
block|{
name|String
name|rtj
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"require.test.libhadoop"
argument_list|)
decl_stmt|;
if|if
condition|(
name|rtj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|rtj
operator|.
name|compareToIgnoreCase
argument_list|(
literal|"false"
argument_list|)
operator|==
literal|0
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Test
DECL|method|testNativeCodeLoaded ()
specifier|public
name|void
name|testNativeCodeLoaded
parameter_list|()
block|{
if|if
condition|(
name|requireTestJni
argument_list|()
operator|==
literal|false
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"TestNativeCodeLoader: libhadoop.so testing is not required."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
name|String
name|LD_LIBRARY_PATH
init|=
name|System
operator|.
name|getenv
argument_list|()
operator|.
name|get
argument_list|(
literal|"LD_LIBRARY_PATH"
argument_list|)
decl_stmt|;
if|if
condition|(
name|LD_LIBRARY_PATH
operator|==
literal|null
condition|)
name|LD_LIBRARY_PATH
operator|=
literal|""
expr_stmt|;
name|fail
argument_list|(
literal|"TestNativeCodeLoader: libhadoop.so testing was required, but "
operator|+
literal|"libhadoop.so was not loaded.  LD_LIBRARY_PATH = "
operator|+
name|LD_LIBRARY_PATH
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"TestHdfsNativeCodeLoader: libhadoop.so is loaded."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

