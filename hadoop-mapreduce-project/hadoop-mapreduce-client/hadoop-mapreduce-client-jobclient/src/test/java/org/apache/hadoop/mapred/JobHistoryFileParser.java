begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|conf
operator|.
name|Configuration
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
name|FileSystem
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
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|jobhistory
operator|.
name|JobHistoryParser
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
name|mapreduce
operator|.
name|jobhistory
operator|.
name|JobHistoryParser
operator|.
name|JobInfo
import|;
end_import

begin_class
DECL|class|JobHistoryFileParser
class|class
name|JobHistoryFileParser
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JobHistoryFileParser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|fs
specifier|private
specifier|final
name|FileSystem
name|fs
decl_stmt|;
DECL|method|JobHistoryFileParser (FileSystem fs)
specifier|public
name|JobHistoryFileParser
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"JobHistoryFileParser created with "
operator|+
name|fs
argument_list|)
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|fs
expr_stmt|;
block|}
DECL|method|parseHistoryFile (Path path)
specifier|public
name|JobInfo
name|parseHistoryFile
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"parsing job history file "
operator|+
name|path
argument_list|)
expr_stmt|;
name|JobHistoryParser
name|parser
init|=
operator|new
name|JobHistoryParser
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
decl_stmt|;
return|return
name|parser
operator|.
name|parse
argument_list|()
return|;
block|}
DECL|method|parseConfiguration (Path path)
specifier|public
name|Configuration
name|parseConfiguration
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"parsing job configuration file "
operator|+
name|path
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
block|}
end_class

end_unit

