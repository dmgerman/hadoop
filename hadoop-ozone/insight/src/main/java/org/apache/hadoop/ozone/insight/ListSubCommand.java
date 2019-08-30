begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.insight
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|insight
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
name|hdds
operator|.
name|cli
operator|.
name|HddsVersionProvider
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_comment
comment|/**  * Subcommand to list of the available insight points.  */
end_comment

begin_class
annotation|@
name|CommandLine
operator|.
name|Command
argument_list|(
name|name
operator|=
literal|"list"
argument_list|,
name|description
operator|=
literal|"Show available insight points."
argument_list|,
name|mixinStandardHelpOptions
operator|=
literal|true
argument_list|,
name|versionProvider
operator|=
name|HddsVersionProvider
operator|.
name|class
argument_list|)
DECL|class|ListSubCommand
specifier|public
class|class
name|ListSubCommand
extends|extends
name|BaseInsightSubCommand
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
block|{
annotation|@
name|CommandLine
operator|.
name|Parameters
argument_list|(
name|defaultValue
operator|=
literal|""
argument_list|)
DECL|field|insightPrefix
specifier|private
name|String
name|insightPrefix
decl_stmt|;
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Available insight points:\n\n"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|InsightPoint
argument_list|>
name|insightPoints
init|=
name|createInsightPoints
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|InsightPoint
argument_list|>
name|entry
range|:
name|insightPoints
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|insightPrefix
operator|==
literal|null
operator|||
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
name|insightPrefix
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"  %-33s    %s"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getDescription
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

