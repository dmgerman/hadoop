begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.snapshot
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|snapshot
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
name|hdfs
operator|.
name|DistributedFileSystem
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
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
name|hdfs
operator|.
name|protocol
operator|.
name|SnapshotDiffReport
import|;
end_import

begin_comment
comment|/**  * A tool used to get the difference report between two snapshots, or between  * a snapshot and the current status of a directory.   *<pre>  * Usage: SnapshotDiff snapshotDir from to  * For from/to, users can use "." to present the current status, and use   * ".snapshot/snapshot_name" to present a snapshot, where ".snapshot/" can be   * omitted.  *</pre>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|SnapshotDiff
specifier|public
class|class
name|SnapshotDiff
block|{
DECL|method|getSnapshotName (String name)
specifier|private
specifier|static
name|String
name|getSnapshotName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|Path
operator|.
name|CUR_DIR
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// current directory
return|return
literal|""
return|;
block|}
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|HdfsConstants
operator|.
name|DOT_SNAPSHOT_DIR
operator|+
name|Path
operator|.
name|SEPARATOR
argument_list|)
operator|||
name|name
operator|.
name|startsWith
argument_list|(
name|Path
operator|.
name|SEPARATOR
operator|+
name|HdfsConstants
operator|.
name|DOT_SNAPSHOT_DIR
operator|+
name|Path
operator|.
name|SEPARATOR
argument_list|)
condition|)
block|{
comment|// get the snapshot name
name|int
name|i
init|=
name|name
operator|.
name|indexOf
argument_list|(
name|HdfsConstants
operator|.
name|DOT_SNAPSHOT_DIR
argument_list|)
decl_stmt|;
return|return
name|name
operator|.
name|substring
argument_list|(
name|i
operator|+
name|HdfsConstants
operator|.
name|DOT_SNAPSHOT_DIR
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
return|;
block|}
return|return
name|name
return|;
block|}
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|description
init|=
literal|"SnapshotDiff<snapshotDir><from><to>:\n"
operator|+
literal|"\tGet the difference between two snapshots, \n"
operator|+
literal|"\tor between a snapshot and the current tree of a directory.\n"
operator|+
literal|"\tFor<from>/<to>, users can use \".\" to present the current status,\n"
operator|+
literal|"\tand use \".snapshot/snapshot_name\" to present a snapshot,\n"
operator|+
literal|"\twhere \".snapshot/\" can be omitted\n"
decl_stmt|;
if|if
condition|(
name|argv
operator|.
name|length
operator|!=
literal|3
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: \n"
operator|+
name|description
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|fs
operator|instanceof
name|DistributedFileSystem
operator|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"SnapshotDiff can only be used in DistributedFileSystem"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|DistributedFileSystem
name|dfs
init|=
operator|(
name|DistributedFileSystem
operator|)
name|fs
decl_stmt|;
name|Path
name|snapshotRoot
init|=
operator|new
name|Path
argument_list|(
name|argv
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|String
name|fromSnapshot
init|=
name|getSnapshotName
argument_list|(
name|argv
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|String
name|toSnapshot
init|=
name|getSnapshotName
argument_list|(
name|argv
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|SnapshotDiffReport
name|diffReport
init|=
name|dfs
operator|.
name|getSnapshotDiffReport
argument_list|(
name|snapshotRoot
argument_list|,
name|fromSnapshot
argument_list|,
name|toSnapshot
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|diffReport
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

