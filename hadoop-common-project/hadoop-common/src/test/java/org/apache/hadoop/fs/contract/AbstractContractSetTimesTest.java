begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|fs
operator|.
name|Path
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Test setTimes -if supported  */
end_comment

begin_class
DECL|class|AbstractContractSetTimesTest
specifier|public
specifier|abstract
class|class
name|AbstractContractSetTimesTest
extends|extends
name|AbstractFSContractTestBase
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractContractSetTimesTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|testPath
specifier|private
name|Path
name|testPath
decl_stmt|;
DECL|field|target
specifier|private
name|Path
name|target
decl_stmt|;
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|skipIfUnsupported
argument_list|(
name|SUPPORTS_SETTIMES
argument_list|)
expr_stmt|;
comment|//delete the test directory
name|testPath
operator|=
name|path
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|target
operator|=
operator|new
name|Path
argument_list|(
name|testPath
argument_list|,
literal|"target"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetTimesNonexistentFile ()
specifier|public
name|void
name|testSetTimesNonexistentFile
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|getFileSystem
argument_list|()
operator|.
name|setTimes
argument_list|(
name|target
argument_list|,
name|time
argument_list|,
name|time
argument_list|)
expr_stmt|;
comment|//got here: trouble
name|fail
argument_list|(
literal|"expected a failure"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|//expected
name|handleExpectedException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

