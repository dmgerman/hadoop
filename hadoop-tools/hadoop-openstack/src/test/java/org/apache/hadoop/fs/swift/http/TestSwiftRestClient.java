begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.http
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
name|http
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|fs
operator|.
name|swift
operator|.
name|SwiftTestConstants
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
name|util
operator|.
name|Duration
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
name|util
operator|.
name|DurationStats
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
name|util
operator|.
name|SwiftObjectPath
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
name|util
operator|.
name|SwiftTestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|Header
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|net
operator|.
name|URI
import|;
end_import

begin_class
DECL|class|TestSwiftRestClient
specifier|public
class|class
name|TestSwiftRestClient
implements|implements
name|SwiftTestConstants
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestSwiftRestClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|runTests
specifier|private
name|boolean
name|runTests
decl_stmt|;
DECL|field|serviceURI
specifier|private
name|URI
name|serviceURI
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|runTests
operator|=
name|SwiftTestUtils
operator|.
name|hasServiceURI
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|runTests
condition|)
block|{
name|serviceURI
operator|=
name|SwiftTestUtils
operator|.
name|getServiceURI
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assumeEnabled ()
specifier|protected
name|void
name|assumeEnabled
parameter_list|()
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|runTests
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testCreate ()
specifier|public
name|void
name|testCreate
parameter_list|()
throws|throws
name|Throwable
block|{
name|assumeEnabled
argument_list|()
expr_stmt|;
name|SwiftRestClient
name|client
init|=
name|createClient
argument_list|()
decl_stmt|;
block|}
DECL|method|createClient ()
specifier|private
name|SwiftRestClient
name|createClient
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|SwiftRestClient
operator|.
name|getInstance
argument_list|(
name|serviceURI
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testAuthenticate ()
specifier|public
name|void
name|testAuthenticate
parameter_list|()
throws|throws
name|Throwable
block|{
name|assumeEnabled
argument_list|()
expr_stmt|;
name|SwiftRestClient
name|client
init|=
name|createClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|authenticate
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
name|SWIFT_TEST_TIMEOUT
argument_list|)
DECL|method|testPutAndDelete ()
specifier|public
name|void
name|testPutAndDelete
parameter_list|()
throws|throws
name|Throwable
block|{
name|assumeEnabled
argument_list|()
expr_stmt|;
name|SwiftRestClient
name|client
init|=
name|createClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|authenticate
argument_list|()
expr_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
literal|"restTestPutAndDelete"
argument_list|)
decl_stmt|;
name|SwiftObjectPath
name|sobject
init|=
name|SwiftObjectPath
operator|.
name|fromPath
argument_list|(
name|serviceURI
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|byte
index|[]
name|stuff
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|stuff
index|[
literal|0
index|]
operator|=
literal|'a'
expr_stmt|;
name|client
operator|.
name|upload
argument_list|(
name|sobject
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|stuff
argument_list|)
argument_list|,
name|stuff
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//check file exists
name|Duration
name|head
init|=
operator|new
name|Duration
argument_list|()
decl_stmt|;
name|Header
index|[]
name|responseHeaders
init|=
name|client
operator|.
name|headRequest
argument_list|(
literal|"expect success"
argument_list|,
name|sobject
argument_list|,
name|SwiftRestClient
operator|.
name|NEWEST
argument_list|)
decl_stmt|;
name|head
operator|.
name|finished
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"head request duration "
operator|+
name|head
argument_list|)
expr_stmt|;
for|for
control|(
name|Header
name|header
range|:
name|responseHeaders
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|header
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//delete the file
name|client
operator|.
name|delete
argument_list|(
name|sobject
argument_list|)
expr_stmt|;
comment|//check file is gone
try|try
block|{
name|Header
index|[]
name|headers
init|=
name|client
operator|.
name|headRequest
argument_list|(
literal|"expect fail"
argument_list|,
name|sobject
argument_list|,
name|SwiftRestClient
operator|.
name|NEWEST
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected deleted file, but object is still present: "
operator|+
name|sobject
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|//expected
block|}
for|for
control|(
name|DurationStats
name|stats
range|:
name|client
operator|.
name|getOperationStatistics
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|stats
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

