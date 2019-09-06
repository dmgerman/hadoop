begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.dynamometer.workloadgenerator.audit
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|dynamometer
operator|.
name|workloadgenerator
operator|.
name|audit
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
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
name|io
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/** Tests for {@link AuditLogDirectParser}. */
end_comment

begin_class
DECL|class|TestAuditLogDirectParser
specifier|public
class|class
name|TestAuditLogDirectParser
block|{
DECL|field|START_TIMESTAMP
specifier|private
specifier|static
specifier|final
name|long
name|START_TIMESTAMP
init|=
literal|10000
decl_stmt|;
DECL|field|parser
specifier|private
name|AuditLogDirectParser
name|parser
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|parser
operator|=
operator|new
name|AuditLogDirectParser
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|AuditLogDirectParser
operator|.
name|AUDIT_START_TIMESTAMP_KEY
argument_list|,
name|START_TIMESTAMP
argument_list|)
expr_stmt|;
name|parser
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|getAuditString (String timestamp, String ugi, String cmd, String src, String dst)
specifier|private
name|Text
name|getAuditString
parameter_list|(
name|String
name|timestamp
parameter_list|,
name|String
name|ugi
parameter_list|,
name|String
name|cmd
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|)
block|{
return|return
operator|new
name|Text
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s INFO FSNamesystem.audit: "
operator|+
literal|"allowed=true\tugi=%s\tip=0.0.0.0\tcmd=%s\tsrc=%s\t"
operator|+
literal|"dst=%s\tperm=null\tproto=rpc"
argument_list|,
name|timestamp
argument_list|,
name|ugi
argument_list|,
name|cmd
argument_list|,
name|src
argument_list|,
name|dst
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testSimpleInput ()
specifier|public
name|void
name|testSimpleInput
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|in
init|=
name|getAuditString
argument_list|(
literal|"1970-01-01 00:00:11,000"
argument_list|,
literal|"fakeUser"
argument_list|,
literal|"listStatus"
argument_list|,
literal|"sourcePath"
argument_list|,
literal|"null"
argument_list|)
decl_stmt|;
name|AuditReplayCommand
name|expected
init|=
operator|new
name|AuditReplayCommand
argument_list|(
literal|1000
argument_list|,
literal|"fakeUser"
argument_list|,
literal|"listStatus"
argument_list|,
literal|"sourcePath"
argument_list|,
literal|"null"
argument_list|,
literal|"0.0.0.0"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
name|in
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInputWithEquals ()
specifier|public
name|void
name|testInputWithEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|in
init|=
name|getAuditString
argument_list|(
literal|"1970-01-01 00:00:11,000"
argument_list|,
literal|"fakeUser"
argument_list|,
literal|"listStatus"
argument_list|,
literal|"day=1970"
argument_list|,
literal|"null"
argument_list|)
decl_stmt|;
name|AuditReplayCommand
name|expected
init|=
operator|new
name|AuditReplayCommand
argument_list|(
literal|1000
argument_list|,
literal|"fakeUser"
argument_list|,
literal|"listStatus"
argument_list|,
literal|"day=1970"
argument_list|,
literal|"null"
argument_list|,
literal|"0.0.0.0"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
name|in
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInputWithRenameOptions ()
specifier|public
name|void
name|testInputWithRenameOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|in
init|=
name|getAuditString
argument_list|(
literal|"1970-01-01 00:00:11,000"
argument_list|,
literal|"fakeUser"
argument_list|,
literal|"rename (options=[TO_TRASH])"
argument_list|,
literal|"sourcePath"
argument_list|,
literal|"destPath"
argument_list|)
decl_stmt|;
name|AuditReplayCommand
name|expected
init|=
operator|new
name|AuditReplayCommand
argument_list|(
literal|1000
argument_list|,
literal|"fakeUser"
argument_list|,
literal|"rename (options=[TO_TRASH])"
argument_list|,
literal|"sourcePath"
argument_list|,
literal|"destPath"
argument_list|,
literal|"0.0.0.0"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
name|in
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInputWithTokenAuth ()
specifier|public
name|void
name|testInputWithTokenAuth
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|in
init|=
name|getAuditString
argument_list|(
literal|"1970-01-01 00:00:11,000"
argument_list|,
literal|"fakeUser (auth:TOKEN)"
argument_list|,
literal|"create"
argument_list|,
literal|"sourcePath"
argument_list|,
literal|"null"
argument_list|)
decl_stmt|;
name|AuditReplayCommand
name|expected
init|=
operator|new
name|AuditReplayCommand
argument_list|(
literal|1000
argument_list|,
literal|"fakeUser"
argument_list|,
literal|"create"
argument_list|,
literal|"sourcePath"
argument_list|,
literal|"null"
argument_list|,
literal|"0.0.0.0"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
name|in
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInputWithProxyUser ()
specifier|public
name|void
name|testInputWithProxyUser
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|in
init|=
name|getAuditString
argument_list|(
literal|"1970-01-01 00:00:11,000"
argument_list|,
literal|"proxyUser (auth:TOKEN) via fakeUser"
argument_list|,
literal|"create"
argument_list|,
literal|"sourcePath"
argument_list|,
literal|"null"
argument_list|)
decl_stmt|;
name|AuditReplayCommand
name|expected
init|=
operator|new
name|AuditReplayCommand
argument_list|(
literal|1000
argument_list|,
literal|"proxyUser"
argument_list|,
literal|"create"
argument_list|,
literal|"sourcePath"
argument_list|,
literal|"null"
argument_list|,
literal|"0.0.0.0"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
name|in
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseDefaultDateFormat ()
specifier|public
name|void
name|testParseDefaultDateFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|in
init|=
name|getAuditString
argument_list|(
literal|"1970-01-01 13:00:00,000"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|)
decl_stmt|;
name|AuditReplayCommand
name|expected
init|=
operator|new
name|AuditReplayCommand
argument_list|(
literal|13
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
operator|-
name|START_TIMESTAMP
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"0.0.0.0"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
name|in
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseCustomDateFormat ()
specifier|public
name|void
name|testParseCustomDateFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|parser
operator|=
operator|new
name|AuditLogDirectParser
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|AuditLogDirectParser
operator|.
name|AUDIT_START_TIMESTAMP_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AuditLogDirectParser
operator|.
name|AUDIT_LOG_DATE_FORMAT_KEY
argument_list|,
literal|"yyyy-MM-dd hh:mm:ss,SSS a"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Text
name|in
init|=
name|getAuditString
argument_list|(
literal|"1970-01-01 01:00:00,000 PM"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|)
decl_stmt|;
name|AuditReplayCommand
name|expected
init|=
operator|new
name|AuditReplayCommand
argument_list|(
literal|13
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"0.0.0.0"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
name|in
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseCustomTimeZone ()
specifier|public
name|void
name|testParseCustomTimeZone
parameter_list|()
throws|throws
name|Exception
block|{
name|parser
operator|=
operator|new
name|AuditLogDirectParser
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|AuditLogDirectParser
operator|.
name|AUDIT_START_TIMESTAMP_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AuditLogDirectParser
operator|.
name|AUDIT_LOG_DATE_TIME_ZONE_KEY
argument_list|,
literal|"Etc/GMT-1"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Text
name|in
init|=
name|getAuditString
argument_list|(
literal|"1970-01-01 01:00:00,000"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|)
decl_stmt|;
name|AuditReplayCommand
name|expected
init|=
operator|new
name|AuditReplayCommand
argument_list|(
literal|0
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"ignored"
argument_list|,
literal|"0.0.0.0"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
name|in
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseCustomAuditLineFormat ()
specifier|public
name|void
name|testParseCustomAuditLineFormat
parameter_list|()
throws|throws
name|Exception
block|{
name|Text
name|auditLine
init|=
operator|new
name|Text
argument_list|(
literal|"CUSTOM FORMAT (1970-01-01 00:00:00,000) "
operator|+
literal|"allowed=true\tugi=fakeUser\tip=0.0.0.0\tcmd=fakeCommand\tsrc=src\t"
operator|+
literal|"dst=null\tperm=null\tproto=rpc"
argument_list|)
decl_stmt|;
name|parser
operator|=
operator|new
name|AuditLogDirectParser
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|AuditLogDirectParser
operator|.
name|AUDIT_START_TIMESTAMP_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|AuditLogDirectParser
operator|.
name|AUDIT_LOG_PARSE_REGEX_KEY
argument_list|,
literal|"CUSTOM FORMAT \\((?<timestamp>.+?)\\) (?<message>.+)"
argument_list|)
expr_stmt|;
name|parser
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|AuditReplayCommand
name|expected
init|=
operator|new
name|AuditReplayCommand
argument_list|(
literal|0
argument_list|,
literal|"fakeUser"
argument_list|,
literal|"fakeCommand"
argument_list|,
literal|"src"
argument_list|,
literal|"null"
argument_list|,
literal|"0.0.0.0"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
name|auditLine
argument_list|,
name|Function
operator|.
name|identity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

