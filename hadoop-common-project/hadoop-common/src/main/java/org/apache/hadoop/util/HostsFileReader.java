begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_comment
comment|// Keeps track of which datanodes/tasktrackers are allowed to connect to the
end_comment

begin_comment
comment|// namenode/jobtracker.
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|HostsFileReader
specifier|public
class|class
name|HostsFileReader
block|{
DECL|field|includes
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|includes
decl_stmt|;
DECL|field|excludes
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|excludes
decl_stmt|;
DECL|field|includesFile
specifier|private
name|String
name|includesFile
decl_stmt|;
DECL|field|excludesFile
specifier|private
name|String
name|excludesFile
decl_stmt|;
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
name|HostsFileReader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|HostsFileReader (String inFile, String exFile)
specifier|public
name|HostsFileReader
parameter_list|(
name|String
name|inFile
parameter_list|,
name|String
name|exFile
parameter_list|)
throws|throws
name|IOException
block|{
name|includes
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|excludes
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|includesFile
operator|=
name|inFile
expr_stmt|;
name|excludesFile
operator|=
name|exFile
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
DECL|method|readFileToSet (String type, String filename, Set<String> set)
specifier|public
specifier|static
name|void
name|readFileToSet
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|filename
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|set
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|filename
argument_list|)
decl_stmt|;
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fis
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|nodes
init|=
name|line
operator|.
name|split
argument_list|(
literal|"[ \t\n\f\r]+"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|nodes
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
comment|// Everything from now on is a comment
break|break;
block|}
if|if
condition|(
operator|!
name|nodes
index|[
name|i
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding "
operator|+
name|nodes
index|[
name|i
index|]
operator|+
literal|" to the list of "
operator|+
name|type
operator|+
literal|" hosts from "
operator|+
name|filename
argument_list|)
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
name|nodes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|refresh ()
specifier|public
specifier|synchronized
name|void
name|refresh
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Refreshing hosts (include/exclude) list"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|includesFile
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|newIncludes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|readFileToSet
argument_list|(
literal|"included"
argument_list|,
name|includesFile
argument_list|,
name|newIncludes
argument_list|)
expr_stmt|;
comment|// switch the new hosts that are to be included
name|includes
operator|=
name|newIncludes
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|excludesFile
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|newExcludes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|readFileToSet
argument_list|(
literal|"excluded"
argument_list|,
name|excludesFile
argument_list|,
name|newExcludes
argument_list|)
expr_stmt|;
comment|// switch the excluded hosts
name|excludes
operator|=
name|newExcludes
expr_stmt|;
block|}
block|}
DECL|method|getHosts ()
specifier|public
specifier|synchronized
name|Set
argument_list|<
name|String
argument_list|>
name|getHosts
parameter_list|()
block|{
return|return
name|includes
return|;
block|}
DECL|method|getExcludedHosts ()
specifier|public
specifier|synchronized
name|Set
argument_list|<
name|String
argument_list|>
name|getExcludedHosts
parameter_list|()
block|{
return|return
name|excludes
return|;
block|}
DECL|method|setIncludesFile (String includesFile)
specifier|public
specifier|synchronized
name|void
name|setIncludesFile
parameter_list|(
name|String
name|includesFile
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting the includes file to "
operator|+
name|includesFile
argument_list|)
expr_stmt|;
name|this
operator|.
name|includesFile
operator|=
name|includesFile
expr_stmt|;
block|}
DECL|method|setExcludesFile (String excludesFile)
specifier|public
specifier|synchronized
name|void
name|setExcludesFile
parameter_list|(
name|String
name|excludesFile
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting the excludes file to "
operator|+
name|excludesFile
argument_list|)
expr_stmt|;
name|this
operator|.
name|excludesFile
operator|=
name|excludesFile
expr_stmt|;
block|}
DECL|method|updateFileNames (String includesFile, String excludesFile)
specifier|public
specifier|synchronized
name|void
name|updateFileNames
parameter_list|(
name|String
name|includesFile
parameter_list|,
name|String
name|excludesFile
parameter_list|)
throws|throws
name|IOException
block|{
name|setIncludesFile
argument_list|(
name|includesFile
argument_list|)
expr_stmt|;
name|setExcludesFile
argument_list|(
name|excludesFile
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

