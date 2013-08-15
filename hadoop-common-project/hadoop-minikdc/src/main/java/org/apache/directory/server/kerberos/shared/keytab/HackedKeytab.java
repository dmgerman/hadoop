begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.directory.server.kerberos.shared.keytab
package|package
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|server
operator|.
name|kerberos
operator|.
name|shared
operator|.
name|keytab
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|//This is a hack for ApacheDS 2.0.0-M14 to be able to create
end_comment

begin_comment
comment|//keytab files with more than one principal.
end_comment

begin_comment
comment|//It needs to be in this package because the KeytabEncoder class is package
end_comment

begin_comment
comment|// private.
end_comment

begin_comment
comment|//This class can be removed once jira DIRSERVER-1882
end_comment

begin_comment
comment|// (https://issues.apache.org/jira/browse/DIRSERVER-1882) solved
end_comment

begin_class
DECL|class|HackedKeytab
specifier|public
class|class
name|HackedKeytab
extends|extends
name|Keytab
block|{
DECL|field|keytabVersion
specifier|private
name|byte
index|[]
name|keytabVersion
init|=
name|VERSION_52
decl_stmt|;
DECL|method|write ( File file, int principalCount )
specifier|public
name|void
name|write
parameter_list|(
name|File
name|file
parameter_list|,
name|int
name|principalCount
parameter_list|)
throws|throws
name|IOException
block|{
name|HackedKeytabEncoder
name|writer
init|=
operator|new
name|HackedKeytabEncoder
argument_list|()
decl_stmt|;
name|ByteBuffer
name|buffer
init|=
name|writer
operator|.
name|write
argument_list|(
name|keytabVersion
argument_list|,
name|getEntries
argument_list|()
argument_list|,
name|principalCount
argument_list|)
decl_stmt|;
name|writeFile
argument_list|(
name|buffer
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

