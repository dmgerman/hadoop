begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|ContentSummary
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
name|FsShell
import|;
end_import

begin_comment
comment|/**  * Count the number of directories, files, bytes, quota, and remaining quota.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|Count
specifier|public
class|class
name|Count
extends|extends
name|FsCommand
block|{
comment|/**    * Register the names for the count command    * @param factory the command factory that will instantiate this class    */
DECL|method|registerCommands (CommandFactory factory)
specifier|public
specifier|static
name|void
name|registerCommands
parameter_list|(
name|CommandFactory
name|factory
parameter_list|)
block|{
name|factory
operator|.
name|addClass
argument_list|(
name|Count
operator|.
name|class
argument_list|,
literal|"-count"
argument_list|)
expr_stmt|;
block|}
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"count"
decl_stmt|;
DECL|field|USAGE
specifier|public
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"[-q]<path> ..."
decl_stmt|;
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"Count the number of directories, files and bytes under the paths\n"
operator|+
literal|"that match the specified file pattern.  The output columns are:\n"
operator|+
literal|"DIR_COUNT FILE_COUNT CONTENT_SIZE FILE_NAME or\n"
operator|+
literal|"QUOTA REMAINING_QUATA SPACE_QUOTA REMAINING_SPACE_QUOTA \n"
operator|+
literal|"      DIR_COUNT FILE_COUNT CONTENT_SIZE FILE_NAME"
decl_stmt|;
DECL|field|showQuotas
specifier|private
name|boolean
name|showQuotas
decl_stmt|;
comment|/** Constructor */
DECL|method|Count ()
specifier|public
name|Count
parameter_list|()
block|{}
comment|/** Constructor    * @deprecated invoke via {@link FsShell}    * @param cmd the count command    * @param pos the starting index of the arguments     * @param conf configuration    */
annotation|@
name|Deprecated
DECL|method|Count (String[] cmd, int pos, Configuration conf)
specifier|public
name|Count
parameter_list|(
name|String
index|[]
name|cmd
parameter_list|,
name|int
name|pos
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|args
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|cmd
argument_list|,
name|pos
argument_list|,
name|cmd
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processOptions (LinkedList<String> args)
specifier|protected
name|void
name|processOptions
parameter_list|(
name|LinkedList
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|"q"
argument_list|)
decl_stmt|;
name|cf
operator|.
name|parse
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// default path is the current working directory
name|args
operator|.
name|add
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
name|showQuotas
operator|=
name|cf
operator|.
name|getOpt
argument_list|(
literal|"q"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|processPath (PathData src)
specifier|protected
name|void
name|processPath
parameter_list|(
name|PathData
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|ContentSummary
name|summary
init|=
name|src
operator|.
name|fs
operator|.
name|getContentSummary
argument_list|(
name|src
operator|.
name|path
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|summary
operator|.
name|toString
argument_list|(
name|showQuotas
argument_list|)
operator|+
name|src
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

