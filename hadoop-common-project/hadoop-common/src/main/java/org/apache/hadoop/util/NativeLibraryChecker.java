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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|compress
operator|.
name|ZStandardCodec
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
name|erasurecode
operator|.
name|ErasureCodeNative
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
name|crypto
operator|.
name|OpensslCipher
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
name|compress
operator|.
name|Lz4Codec
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
name|compress
operator|.
name|SnappyCodec
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
name|compress
operator|.
name|bzip2
operator|.
name|Bzip2Factory
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
name|compress
operator|.
name|zlib
operator|.
name|ZlibFactory
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|NativeLibraryChecker
specifier|public
class|class
name|NativeLibraryChecker
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NativeLibraryChecker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * A tool to test native library availability,     */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|String
name|usage
init|=
literal|"NativeLibraryChecker [-a|-h]\n"
operator|+
literal|"  -a  use -a to check all libraries are available\n"
operator|+
literal|"      by default just check hadoop library (and\n"
operator|+
literal|"      winutils.exe on Windows OS) is available\n"
operator|+
literal|"      exit with error code 1 if check failed\n"
operator|+
literal|"  -h  print this message\n"
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|1
operator|||
operator|(
name|args
operator|.
name|length
operator|==
literal|1
operator|&&
operator|!
operator|(
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"-a"
argument_list|)
operator|||
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"-h"
argument_list|)
operator|)
operator|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|boolean
name|checkAll
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"-h"
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
return|return;
block|}
name|checkAll
operator|=
literal|true
expr_stmt|;
block|}
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|boolean
name|nativeHadoopLoaded
init|=
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
decl_stmt|;
name|boolean
name|zlibLoaded
init|=
literal|false
decl_stmt|;
name|boolean
name|snappyLoaded
init|=
literal|false
decl_stmt|;
name|boolean
name|isalLoaded
init|=
literal|false
decl_stmt|;
name|boolean
name|zStdLoaded
init|=
literal|false
decl_stmt|;
comment|// lz4 is linked within libhadoop
name|boolean
name|lz4Loaded
init|=
name|nativeHadoopLoaded
decl_stmt|;
name|boolean
name|bzip2Loaded
init|=
name|Bzip2Factory
operator|.
name|isNativeBzip2Loaded
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|boolean
name|openSslLoaded
init|=
literal|false
decl_stmt|;
name|boolean
name|winutilsExists
init|=
literal|false
decl_stmt|;
name|String
name|openSslDetail
init|=
literal|""
decl_stmt|;
name|String
name|hadoopLibraryName
init|=
literal|""
decl_stmt|;
name|String
name|zlibLibraryName
init|=
literal|""
decl_stmt|;
name|String
name|snappyLibraryName
init|=
literal|""
decl_stmt|;
name|String
name|isalDetail
init|=
literal|""
decl_stmt|;
name|String
name|zstdLibraryName
init|=
literal|""
decl_stmt|;
name|String
name|lz4LibraryName
init|=
literal|""
decl_stmt|;
name|String
name|bzip2LibraryName
init|=
literal|""
decl_stmt|;
name|String
name|winutilsPath
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nativeHadoopLoaded
condition|)
block|{
name|hadoopLibraryName
operator|=
name|NativeCodeLoader
operator|.
name|getLibraryName
argument_list|()
expr_stmt|;
name|zlibLoaded
operator|=
name|ZlibFactory
operator|.
name|isNativeZlibLoaded
argument_list|(
name|conf
argument_list|)
expr_stmt|;
if|if
condition|(
name|zlibLoaded
condition|)
block|{
name|zlibLibraryName
operator|=
name|ZlibFactory
operator|.
name|getLibraryName
argument_list|()
expr_stmt|;
block|}
name|zStdLoaded
operator|=
name|NativeCodeLoader
operator|.
name|buildSupportsZstd
argument_list|()
operator|&&
name|ZStandardCodec
operator|.
name|isNativeCodeLoaded
argument_list|()
expr_stmt|;
if|if
condition|(
name|zStdLoaded
operator|&&
name|NativeCodeLoader
operator|.
name|buildSupportsZstd
argument_list|()
condition|)
block|{
name|zstdLibraryName
operator|=
name|ZStandardCodec
operator|.
name|getLibraryName
argument_list|()
expr_stmt|;
block|}
name|snappyLoaded
operator|=
name|NativeCodeLoader
operator|.
name|buildSupportsSnappy
argument_list|()
operator|&&
name|SnappyCodec
operator|.
name|isNativeCodeLoaded
argument_list|()
expr_stmt|;
if|if
condition|(
name|snappyLoaded
operator|&&
name|NativeCodeLoader
operator|.
name|buildSupportsSnappy
argument_list|()
condition|)
block|{
name|snappyLibraryName
operator|=
name|SnappyCodec
operator|.
name|getLibraryName
argument_list|()
expr_stmt|;
block|}
name|isalDetail
operator|=
name|ErasureCodeNative
operator|.
name|getLoadingFailureReason
argument_list|()
expr_stmt|;
if|if
condition|(
name|isalDetail
operator|!=
literal|null
condition|)
block|{
name|isalLoaded
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|isalDetail
operator|=
name|ErasureCodeNative
operator|.
name|getLibraryName
argument_list|()
expr_stmt|;
name|isalLoaded
operator|=
literal|true
expr_stmt|;
block|}
name|openSslDetail
operator|=
name|OpensslCipher
operator|.
name|getLoadingFailureReason
argument_list|()
expr_stmt|;
if|if
condition|(
name|openSslDetail
operator|!=
literal|null
condition|)
block|{
name|openSslLoaded
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|openSslDetail
operator|=
name|OpensslCipher
operator|.
name|getLibraryName
argument_list|()
expr_stmt|;
name|openSslLoaded
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|lz4Loaded
condition|)
block|{
name|lz4LibraryName
operator|=
name|Lz4Codec
operator|.
name|getLibraryName
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|bzip2Loaded
condition|)
block|{
name|bzip2LibraryName
operator|=
name|Bzip2Factory
operator|.
name|getLibraryName
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
comment|// winutils.exe is required on Windows
try|try
block|{
name|winutilsPath
operator|=
name|Shell
operator|.
name|getWinUtilsFile
argument_list|()
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
name|winutilsExists
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No Winutils: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|winutilsPath
operator|=
name|e
operator|.
name|getMessage
argument_list|()
expr_stmt|;
name|winutilsExists
operator|=
literal|false
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"winutils: %b %s%n"
argument_list|,
name|winutilsExists
argument_list|,
name|winutilsPath
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Native library checking:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"hadoop:  %b %s%n"
argument_list|,
name|nativeHadoopLoaded
argument_list|,
name|hadoopLibraryName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"zlib:    %b %s%n"
argument_list|,
name|zlibLoaded
argument_list|,
name|zlibLibraryName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"zstd  :  %b %s%n"
argument_list|,
name|zStdLoaded
argument_list|,
name|zstdLibraryName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"snappy:  %b %s%n"
argument_list|,
name|snappyLoaded
argument_list|,
name|snappyLibraryName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"lz4:     %b %s%n"
argument_list|,
name|lz4Loaded
argument_list|,
name|lz4LibraryName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"bzip2:   %b %s%n"
argument_list|,
name|bzip2Loaded
argument_list|,
name|bzip2LibraryName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"openssl: %b %s%n"
argument_list|,
name|openSslLoaded
argument_list|,
name|openSslDetail
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"ISA-L:   %b %s%n"
argument_list|,
name|isalLoaded
argument_list|,
name|isalDetail
argument_list|)
expr_stmt|;
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"winutils: %b %s%n"
argument_list|,
name|winutilsExists
argument_list|,
name|winutilsPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
operator|!
name|nativeHadoopLoaded
operator|)
operator|||
operator|(
name|Shell
operator|.
name|WINDOWS
operator|&&
operator|(
operator|!
name|winutilsExists
operator|)
operator|)
operator|||
operator|(
name|checkAll
operator|&&
operator|!
operator|(
name|zlibLoaded
operator|&&
name|snappyLoaded
operator|&&
name|lz4Loaded
operator|&&
name|bzip2Loaded
operator|&&
name|isalLoaded
operator|&&
name|zStdLoaded
operator|)
operator|)
condition|)
block|{
comment|// return 1 to indicated check failed
name|ExitUtil
operator|.
name|terminate
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

