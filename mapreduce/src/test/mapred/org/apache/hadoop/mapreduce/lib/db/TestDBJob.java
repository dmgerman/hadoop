begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.db
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|db
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|examples
operator|.
name|DBCountPageView
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
name|mapred
operator|.
name|HadoopTestCase
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
name|ToolRunner
import|;
end_import

begin_class
DECL|class|TestDBJob
specifier|public
class|class
name|TestDBJob
extends|extends
name|HadoopTestCase
block|{
DECL|method|TestDBJob ()
specifier|public
name|TestDBJob
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|LOCAL_MR
argument_list|,
name|LOCAL_FS
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testRun ()
specifier|public
name|void
name|testRun
parameter_list|()
throws|throws
name|Exception
block|{
name|DBCountPageView
name|testDriver
init|=
operator|new
name|DBCountPageView
argument_list|()
decl_stmt|;
name|ToolRunner
operator|.
name|run
argument_list|(
name|createJobConf
argument_list|()
argument_list|,
name|testDriver
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

