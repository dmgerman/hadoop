begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|TestArrayFile
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
name|io
operator|.
name|TestSetFile
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
name|ipc
operator|.
name|TestIPC
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
name|ipc
operator|.
name|TestRPC
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
name|ProgramDriver
import|;
end_import

begin_comment
comment|/**  * Driver for core tests.  */
end_comment

begin_class
DECL|class|CoreTestDriver
specifier|public
class|class
name|CoreTestDriver
block|{
DECL|field|pgd
specifier|private
name|ProgramDriver
name|pgd
decl_stmt|;
DECL|method|CoreTestDriver ()
specifier|public
name|CoreTestDriver
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ProgramDriver
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|CoreTestDriver (ProgramDriver pgd)
specifier|public
name|CoreTestDriver
parameter_list|(
name|ProgramDriver
name|pgd
parameter_list|)
block|{
name|this
operator|.
name|pgd
operator|=
name|pgd
expr_stmt|;
try|try
block|{
name|pgd
operator|.
name|addClass
argument_list|(
literal|"testsetfile"
argument_list|,
name|TestSetFile
operator|.
name|class
argument_list|,
literal|"A test for flat files of binary key/value pairs."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"testarrayfile"
argument_list|,
name|TestArrayFile
operator|.
name|class
argument_list|,
literal|"A test for flat files of binary key/value pairs."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"testrpc"
argument_list|,
name|TestRPC
operator|.
name|class
argument_list|,
literal|"A test for rpc."
argument_list|)
expr_stmt|;
name|pgd
operator|.
name|addClass
argument_list|(
literal|"testipc"
argument_list|,
name|TestIPC
operator|.
name|class
argument_list|,
literal|"A test for ipc."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|run (String argv[])
specifier|public
name|void
name|run
parameter_list|(
name|String
name|argv
index|[]
parameter_list|)
block|{
try|try
block|{
name|pgd
operator|.
name|driver
argument_list|(
name|argv
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|main (String argv[])
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|argv
index|[]
parameter_list|)
block|{
operator|new
name|CoreTestDriver
argument_list|()
operator|.
name|run
argument_list|(
name|argv
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

