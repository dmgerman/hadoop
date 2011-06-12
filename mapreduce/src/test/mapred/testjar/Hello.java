begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|testjar
package|package
name|testjar
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A simple Hello class that is called from TestRunJar   *  */
end_comment

begin_class
DECL|class|Hello
specifier|public
class|class
name|Hello
block|{
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Creating file"
operator|+
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|FileOutputStream
name|fstream
init|=
operator|new
name|FileOutputStream
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|fstream
operator|.
name|write
argument_list|(
literal|"Hello Hadoopers"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fstream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//do nothing
block|}
block|}
block|}
end_class

end_unit

