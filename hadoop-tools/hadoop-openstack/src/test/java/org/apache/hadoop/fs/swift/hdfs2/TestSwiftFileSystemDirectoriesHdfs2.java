begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.hdfs2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
operator|.
name|hdfs2
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
name|fs
operator|.
name|swift
operator|.
name|TestSwiftFileSystemDirectories
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
name|swift
operator|.
name|snative
operator|.
name|SwiftFileStatus
import|;
end_import

begin_comment
comment|/**  * Add some HDFS-2 only assertions to {@link TestSwiftFileSystemDirectories}  */
end_comment

begin_class
DECL|class|TestSwiftFileSystemDirectoriesHdfs2
specifier|public
class|class
name|TestSwiftFileSystemDirectoriesHdfs2
extends|extends
name|TestSwiftFileSystemDirectories
block|{
comment|/**    * make assertions about fields that only appear in    * FileStatus in HDFS2    * @param stat status to look at    */
DECL|method|extraStatusAssertions (SwiftFileStatus stat)
specifier|protected
name|void
name|extraStatusAssertions
parameter_list|(
name|SwiftFileStatus
name|stat
parameter_list|)
block|{
comment|//HDFS2
name|assertTrue
argument_list|(
literal|"isDirectory(): Not a directory: "
operator|+
name|stat
argument_list|,
name|stat
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"isFile(): declares itself a file: "
operator|+
name|stat
argument_list|,
name|stat
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"isFile(): declares itself a file: "
operator|+
name|stat
argument_list|,
name|stat
operator|.
name|isSymlink
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

