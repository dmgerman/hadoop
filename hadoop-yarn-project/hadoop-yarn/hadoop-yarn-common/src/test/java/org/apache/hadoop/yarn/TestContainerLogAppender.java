begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|PatternLayout
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
DECL|class|TestContainerLogAppender
specifier|public
class|class
name|TestContainerLogAppender
block|{
annotation|@
name|Test
DECL|method|testAppendInClose ()
specifier|public
name|void
name|testAppendInClose
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ContainerLogAppender
name|claAppender
init|=
operator|new
name|ContainerLogAppender
argument_list|()
decl_stmt|;
name|claAppender
operator|.
name|setName
argument_list|(
literal|"testCLA"
argument_list|)
expr_stmt|;
name|claAppender
operator|.
name|setLayout
argument_list|(
operator|new
name|PatternLayout
argument_list|(
literal|"%-5p [%t]: %m%n"
argument_list|)
argument_list|)
expr_stmt|;
name|claAppender
operator|.
name|setContainerLogDir
argument_list|(
literal|"target/testAppendInClose/logDir"
argument_list|)
expr_stmt|;
name|claAppender
operator|.
name|setContainerLogFile
argument_list|(
literal|"syslog"
argument_list|)
expr_stmt|;
name|claAppender
operator|.
name|setTotalLogFileSize
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|claAppender
operator|.
name|activateOptions
argument_list|()
expr_stmt|;
specifier|final
name|Logger
name|claLog
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"testAppendInClose-catergory"
argument_list|)
decl_stmt|;
name|claLog
operator|.
name|setAdditivity
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|claLog
operator|.
name|addAppender
argument_list|(
name|claAppender
argument_list|)
expr_stmt|;
name|claLog
operator|.
name|info
argument_list|(
operator|new
name|Object
argument_list|()
block|{
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|claLog
operator|.
name|info
argument_list|(
literal|"message1"
argument_list|)
expr_stmt|;
return|return
literal|"return message1"
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|claAppender
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

