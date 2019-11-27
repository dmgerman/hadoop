begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.constants
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|constants
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
name|util
operator|.
name|VersionInfo
import|;
end_import

begin_comment
comment|/**  * Responsible to keep all constant keys used in abfs rest client here.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AbfsHttpConstants
specifier|public
specifier|final
class|class
name|AbfsHttpConstants
block|{
comment|// Abfs Http client constants
DECL|field|FILESYSTEM
specifier|public
specifier|static
specifier|final
name|String
name|FILESYSTEM
init|=
literal|"filesystem"
decl_stmt|;
DECL|field|FILE
specifier|public
specifier|static
specifier|final
name|String
name|FILE
init|=
literal|"file"
decl_stmt|;
DECL|field|DIRECTORY
specifier|public
specifier|static
specifier|final
name|String
name|DIRECTORY
init|=
literal|"directory"
decl_stmt|;
DECL|field|APPEND_ACTION
specifier|public
specifier|static
specifier|final
name|String
name|APPEND_ACTION
init|=
literal|"append"
decl_stmt|;
DECL|field|FLUSH_ACTION
specifier|public
specifier|static
specifier|final
name|String
name|FLUSH_ACTION
init|=
literal|"flush"
decl_stmt|;
DECL|field|SET_PROPERTIES_ACTION
specifier|public
specifier|static
specifier|final
name|String
name|SET_PROPERTIES_ACTION
init|=
literal|"setProperties"
decl_stmt|;
DECL|field|SET_ACCESS_CONTROL
specifier|public
specifier|static
specifier|final
name|String
name|SET_ACCESS_CONTROL
init|=
literal|"setAccessControl"
decl_stmt|;
DECL|field|GET_ACCESS_CONTROL
specifier|public
specifier|static
specifier|final
name|String
name|GET_ACCESS_CONTROL
init|=
literal|"getAccessControl"
decl_stmt|;
DECL|field|CHECK_ACCESS
specifier|public
specifier|static
specifier|final
name|String
name|CHECK_ACCESS
init|=
literal|"checkAccess"
decl_stmt|;
DECL|field|GET_STATUS
specifier|public
specifier|static
specifier|final
name|String
name|GET_STATUS
init|=
literal|"getStatus"
decl_stmt|;
DECL|field|DEFAULT_TIMEOUT
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_TIMEOUT
init|=
literal|"90"
decl_stmt|;
DECL|field|TOKEN_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_VERSION
init|=
literal|"2"
decl_stmt|;
DECL|field|JAVA_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|JAVA_VERSION
init|=
literal|"java.version"
decl_stmt|;
DECL|field|OS_NAME
specifier|public
specifier|static
specifier|final
name|String
name|OS_NAME
init|=
literal|"os.name"
decl_stmt|;
DECL|field|OS_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|OS_VERSION
init|=
literal|"os.version"
decl_stmt|;
DECL|field|CLIENT_VERSION
specifier|public
specifier|static
specifier|final
name|String
name|CLIENT_VERSION
init|=
literal|"Azure Blob FS/"
operator|+
name|VersionInfo
operator|.
name|getVersion
argument_list|()
decl_stmt|;
comment|// Abfs Http Verb
DECL|field|HTTP_METHOD_DELETE
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_METHOD_DELETE
init|=
literal|"DELETE"
decl_stmt|;
DECL|field|HTTP_METHOD_GET
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_METHOD_GET
init|=
literal|"GET"
decl_stmt|;
DECL|field|HTTP_METHOD_HEAD
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_METHOD_HEAD
init|=
literal|"HEAD"
decl_stmt|;
DECL|field|HTTP_METHOD_PATCH
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_METHOD_PATCH
init|=
literal|"PATCH"
decl_stmt|;
DECL|field|HTTP_METHOD_POST
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_METHOD_POST
init|=
literal|"POST"
decl_stmt|;
DECL|field|HTTP_METHOD_PUT
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_METHOD_PUT
init|=
literal|"PUT"
decl_stmt|;
comment|// Abfs generic constants
DECL|field|SINGLE_WHITE_SPACE
specifier|public
specifier|static
specifier|final
name|String
name|SINGLE_WHITE_SPACE
init|=
literal|" "
decl_stmt|;
DECL|field|EMPTY_STRING
specifier|public
specifier|static
specifier|final
name|String
name|EMPTY_STRING
init|=
literal|""
decl_stmt|;
DECL|field|FORWARD_SLASH
specifier|public
specifier|static
specifier|final
name|String
name|FORWARD_SLASH
init|=
literal|"/"
decl_stmt|;
DECL|field|DOT
specifier|public
specifier|static
specifier|final
name|String
name|DOT
init|=
literal|"."
decl_stmt|;
DECL|field|PLUS
specifier|public
specifier|static
specifier|final
name|String
name|PLUS
init|=
literal|"+"
decl_stmt|;
DECL|field|STAR
specifier|public
specifier|static
specifier|final
name|String
name|STAR
init|=
literal|"*"
decl_stmt|;
DECL|field|COMMA
specifier|public
specifier|static
specifier|final
name|String
name|COMMA
init|=
literal|","
decl_stmt|;
DECL|field|COLON
specifier|public
specifier|static
specifier|final
name|String
name|COLON
init|=
literal|":"
decl_stmt|;
DECL|field|EQUAL
specifier|public
specifier|static
specifier|final
name|String
name|EQUAL
init|=
literal|"="
decl_stmt|;
DECL|field|QUESTION_MARK
specifier|public
specifier|static
specifier|final
name|String
name|QUESTION_MARK
init|=
literal|"?"
decl_stmt|;
DECL|field|AND_MARK
specifier|public
specifier|static
specifier|final
name|String
name|AND_MARK
init|=
literal|"&"
decl_stmt|;
DECL|field|SEMICOLON
specifier|public
specifier|static
specifier|final
name|String
name|SEMICOLON
init|=
literal|";"
decl_stmt|;
DECL|field|AT
specifier|public
specifier|static
specifier|final
name|String
name|AT
init|=
literal|"@"
decl_stmt|;
DECL|field|HTTP_HEADER_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|HTTP_HEADER_PREFIX
init|=
literal|"x-ms-"
decl_stmt|;
DECL|field|PLUS_ENCODE
specifier|public
specifier|static
specifier|final
name|String
name|PLUS_ENCODE
init|=
literal|"%20"
decl_stmt|;
DECL|field|FORWARD_SLASH_ENCODE
specifier|public
specifier|static
specifier|final
name|String
name|FORWARD_SLASH_ENCODE
init|=
literal|"%2F"
decl_stmt|;
DECL|field|AZURE_DISTRIBUTED_FILE_SYSTEM_AUTHORITY_DELIMITER
specifier|public
specifier|static
specifier|final
name|String
name|AZURE_DISTRIBUTED_FILE_SYSTEM_AUTHORITY_DELIMITER
init|=
literal|"@"
decl_stmt|;
DECL|field|UTF_8
specifier|public
specifier|static
specifier|final
name|String
name|UTF_8
init|=
literal|"utf-8"
decl_stmt|;
DECL|field|GMT_TIMEZONE
specifier|public
specifier|static
specifier|final
name|String
name|GMT_TIMEZONE
init|=
literal|"GMT"
decl_stmt|;
DECL|field|APPLICATION_JSON
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_JSON
init|=
literal|"application/json"
decl_stmt|;
DECL|field|APPLICATION_OCTET_STREAM
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_OCTET_STREAM
init|=
literal|"application/octet-stream"
decl_stmt|;
DECL|field|ROOT_PATH
specifier|public
specifier|static
specifier|final
name|String
name|ROOT_PATH
init|=
literal|"/"
decl_stmt|;
DECL|field|ACCESS_MASK
specifier|public
specifier|static
specifier|final
name|String
name|ACCESS_MASK
init|=
literal|"mask:"
decl_stmt|;
DECL|field|ACCESS_USER
specifier|public
specifier|static
specifier|final
name|String
name|ACCESS_USER
init|=
literal|"user:"
decl_stmt|;
DECL|field|ACCESS_GROUP
specifier|public
specifier|static
specifier|final
name|String
name|ACCESS_GROUP
init|=
literal|"group:"
decl_stmt|;
DECL|field|ACCESS_OTHER
specifier|public
specifier|static
specifier|final
name|String
name|ACCESS_OTHER
init|=
literal|"other:"
decl_stmt|;
DECL|field|DEFAULT_MASK
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_MASK
init|=
literal|"default:mask:"
decl_stmt|;
DECL|field|DEFAULT_USER
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_USER
init|=
literal|"default:user:"
decl_stmt|;
DECL|field|DEFAULT_GROUP
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_GROUP
init|=
literal|"default:group:"
decl_stmt|;
DECL|field|DEFAULT_OTHER
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_OTHER
init|=
literal|"default:other:"
decl_stmt|;
DECL|field|DEFAULT_SCOPE
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_SCOPE
init|=
literal|"default:"
decl_stmt|;
DECL|field|PERMISSION_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|PERMISSION_FORMAT
init|=
literal|"%04d"
decl_stmt|;
DECL|field|SUPER_USER
specifier|public
specifier|static
specifier|final
name|String
name|SUPER_USER
init|=
literal|"$superuser"
decl_stmt|;
DECL|field|CHAR_FORWARD_SLASH
specifier|public
specifier|static
specifier|final
name|char
name|CHAR_FORWARD_SLASH
init|=
literal|'/'
decl_stmt|;
DECL|field|CHAR_EXCLAMATION_POINT
specifier|public
specifier|static
specifier|final
name|char
name|CHAR_EXCLAMATION_POINT
init|=
literal|'!'
decl_stmt|;
DECL|field|CHAR_UNDERSCORE
specifier|public
specifier|static
specifier|final
name|char
name|CHAR_UNDERSCORE
init|=
literal|'_'
decl_stmt|;
DECL|field|CHAR_HYPHEN
specifier|public
specifier|static
specifier|final
name|char
name|CHAR_HYPHEN
init|=
literal|'-'
decl_stmt|;
DECL|field|CHAR_EQUALS
specifier|public
specifier|static
specifier|final
name|char
name|CHAR_EQUALS
init|=
literal|'='
decl_stmt|;
DECL|field|CHAR_STAR
specifier|public
specifier|static
specifier|final
name|char
name|CHAR_STAR
init|=
literal|'*'
decl_stmt|;
DECL|field|CHAR_PLUS
specifier|public
specifier|static
specifier|final
name|char
name|CHAR_PLUS
init|=
literal|'+'
decl_stmt|;
DECL|method|AbfsHttpConstants ()
specifier|private
name|AbfsHttpConstants
parameter_list|()
block|{}
block|}
end_class

end_unit

