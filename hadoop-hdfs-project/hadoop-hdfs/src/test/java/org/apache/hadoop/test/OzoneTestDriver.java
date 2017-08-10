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
name|ozone
operator|.
name|tools
operator|.
name|Corona
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
comment|/**  * Driver for Ozone tests.  */
end_comment

begin_class
DECL|class|OzoneTestDriver
specifier|public
class|class
name|OzoneTestDriver
block|{
DECL|field|pgd
specifier|private
specifier|final
name|ProgramDriver
name|pgd
decl_stmt|;
DECL|method|OzoneTestDriver ()
specifier|public
name|OzoneTestDriver
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
DECL|method|OzoneTestDriver (ProgramDriver pgd)
specifier|public
name|OzoneTestDriver
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
literal|"corona"
argument_list|,
name|Corona
operator|.
name|class
argument_list|,
literal|"Populates ozone with data."
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
DECL|method|run (String[] args)
specifier|public
name|void
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|int
name|exitCode
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|exitCode
operator|=
name|pgd
operator|.
name|run
argument_list|(
name|args
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
name|System
operator|.
name|exit
argument_list|(
name|exitCode
argument_list|)
expr_stmt|;
block|}
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
operator|new
name|OzoneTestDriver
argument_list|()
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

