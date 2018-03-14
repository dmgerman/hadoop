begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
package|;
end_package

begin_comment
comment|/**  * The content types such as file, directory and symlink to be computed.  */
end_comment

begin_enum
DECL|enum|Content
specifier|public
enum|enum
name|Content
block|{
comment|/** The number of files. */
DECL|enumConstant|FILE
name|FILE
block|,
comment|/** The number of directories. */
DECL|enumConstant|DIRECTORY
name|DIRECTORY
block|,
comment|/** The number of symlinks. */
DECL|enumConstant|SYMLINK
name|SYMLINK
block|,
comment|/** The total of file length in bytes. */
DECL|enumConstant|LENGTH
name|LENGTH
block|,
comment|/** The total of disk space usage in bytes including replication. */
DECL|enumConstant|DISKSPACE
name|DISKSPACE
block|,
comment|/** The number of snapshots. */
DECL|enumConstant|SNAPSHOT
name|SNAPSHOT
block|,
comment|/** The number of snapshottable directories. */
DECL|enumConstant|SNAPSHOTTABLE_DIRECTORY
name|SNAPSHOTTABLE_DIRECTORY
block|; }
end_enum

end_unit

