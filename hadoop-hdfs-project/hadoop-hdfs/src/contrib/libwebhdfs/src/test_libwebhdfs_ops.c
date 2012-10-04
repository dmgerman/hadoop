/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "hdfs.h"

#include <inttypes.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>

void permission_disp(short permissions, char *rtr) {
    rtr[9] = '\0';
    int i;
    for(i=2;i>=0;i--)
    {
        short permissionsId = permissions >> (i * 3) & (short)7;
        char* perm;
        switch(permissionsId) {
            case 7:
                perm = "rwx"; break;
            case 6:
                perm = "rw-"; break;
            case 5:
                perm = "r-x"; break;
            case 4:
                perm = "r--"; break;
            case 3:
                perm = "-wx"; break;
            case 2:
                perm = "-w-"; break;
            case 1:
                perm = "--x"; break;
            case 0:
                perm = "---"; break;
            default:
                perm = "???";
        }
        strncpy(rtr, perm, 3);
        rtr+=3;
    }
}

int main(int argc, char **argv) {
    if (argc != 2) {
        fprintf(stderr, "usage: test_libwebhdfs_ops <username>\n");
        return -1;
    }
    
    char buffer[32];
    tSize num_written_bytes;
    
    hdfsFS fs = hdfsConnectAsUserNewInstance("default", 50070, argv[1]);
    if(!fs) {
        fprintf(stderr, "Oops! Failed to connect to hdfs!\n");
        exit(-1);
    }
    
    const char* writePath = "/tmp/testfile.txt";
    const char* fileContents = "Hello, World!";
    
    {
        //Write tests
        
        hdfsFile writeFile = hdfsOpenFile(fs, writePath, O_WRONLY|O_CREAT, 0, 0, 0);
        if(!writeFile) {
            fprintf(stderr, "Failed to open %s for writing!\n", writePath);
            exit(-1);
        }
        fprintf(stderr, "Opened %s for writing successfully...\n", writePath);
        num_written_bytes = hdfsWrite(fs, writeFile, (void*)fileContents, strlen(fileContents) + 1);
        if (num_written_bytes != strlen(fileContents) + 1) {
            fprintf(stderr, "Failed to write correct number of bytes - expected %d, got %d\n",
                    (int)(strlen(fileContents) + 1), (int)num_written_bytes);
            exit(-1);
        }
        fprintf(stderr, "Wrote %d bytes\n", num_written_bytes);
        
        tOffset currentPos = -1;
        if ((currentPos = hdfsTell(fs, writeFile)) == -1) {
            fprintf(stderr,
                    "Failed to get current file position correctly! Got %lld!\n",
                    currentPos);
            exit(-1);
        }
        fprintf(stderr, "Current position: %lld\n", currentPos);
        
        if (hdfsFlush(fs, writeFile)) {
            fprintf(stderr, "Failed to 'flush' %s\n", writePath);
            exit(-1);
        }
        fprintf(stderr, "Flushed %s successfully!\n", writePath);
        
        if (hdfsHFlush(fs, writeFile)) {
            fprintf(stderr, "Failed to 'hflush' %s\n", writePath);
            exit(-1);
        }
        fprintf(stderr, "HFlushed %s successfully!\n", writePath);
        
        hdfsCloseFile(fs, writeFile);
    }
    
    {
        //Read tests
        sleep(1);
        const char* readPath = "/tmp/testfile.txt";
        int exists = hdfsExists(fs, readPath);
        
        if (exists) {
            fprintf(stderr, "Failed to validate existence of %s\n", readPath);
            exists = hdfsExists(fs, readPath);
            if (exists) {
                fprintf(stderr, "Still failed to validate existence of %s\n", readPath);
                exit(-1);
            }
        }
        
        hdfsFile readFile = hdfsOpenFile(fs, readPath, O_RDONLY, 0, 0, 0);
        if (!readFile) {
            fprintf(stderr, "Failed to open %s for reading!\n", readPath);
            exit(-1);
        }
        
        if (!hdfsFileIsOpenForRead(readFile)) {
            fprintf(stderr, "hdfsFileIsOpenForRead: we just opened a file "
                    "with O_RDONLY, and it did not show up as 'open for "
                    "read'\n");
            exit(-1);
        }
        
        fprintf(stderr, "hdfsAvailable: %d\n", hdfsAvailable(fs, readFile));
        
        tOffset seekPos = 1;
        if(hdfsSeek(fs, readFile, seekPos)) {
            fprintf(stderr, "Failed to seek %s for reading!\n", readPath);
            exit(-1);
        }
        
        tOffset currentPos = -1;
        if((currentPos = hdfsTell(fs, readFile)) != seekPos) {
            fprintf(stderr,
                    "Failed to get current file position correctly! Got %lld!\n",
                    currentPos);
            exit(-1);
        }
        fprintf(stderr, "Current position: %lld\n", currentPos);
        
        if (!hdfsFileUsesDirectRead(readFile)) {
            fprintf(stderr, "Direct read support incorrectly not detected "
                    "for HDFS filesystem\n");
            exit(-1);
        }
        
        fprintf(stderr, "Direct read support detected for HDFS\n");
        
        // Test the direct read path
        if(hdfsSeek(fs, readFile, 0)) {
            fprintf(stderr, "Failed to seek %s for reading!\n", readPath);
            exit(-1);
        }
        memset(buffer, 0, sizeof(buffer));
        tSize num_read_bytes = hdfsRead(fs, readFile, (void*)buffer,
                                        sizeof(buffer));
        if (strncmp(fileContents, buffer, strlen(fileContents)) != 0) {
            fprintf(stderr, "Failed to read (direct). Expected %s but got %s (%d bytes)\n",
                    fileContents, buffer, num_read_bytes);
            exit(-1);
        }
        fprintf(stderr, "Read following %d bytes:\n%s\n",
                num_read_bytes, buffer);
        if (hdfsSeek(fs, readFile, 0L)) {
            fprintf(stderr, "Failed to seek to file start!\n");
            exit(-1);
        }
        
        // Disable the direct read path so that we really go through the slow
        // read path
        hdfsFileDisableDirectRead(readFile);
        
        num_read_bytes = hdfsRead(fs, readFile, (void*)buffer,
                                  sizeof(buffer));
        fprintf(stderr, "Read following %d bytes:\n%s\n",
                num_read_bytes, buffer);
        
        memset(buffer, 0, strlen(fileContents + 1));
        
        num_read_bytes = hdfsPread(fs, readFile, 0, (void*)buffer,
                                   sizeof(buffer));
        fprintf(stderr, "Read following %d bytes:\n%s\n",
                num_read_bytes, buffer);
        
        hdfsCloseFile(fs, readFile);
    }
    
    int totalResult = 0;
    int result = 0;
    {
        //Generic file-system operations
        
        const char* srcPath = "/tmp/testfile.txt";
        const char* dstPath = "/tmp/testfile2.txt";
        const char* copyPath = "/tmp/testfile_copy.txt";
        const char* movePath = "/tmp/testfile_move.txt";
        
        fprintf(stderr, "hdfsCopy: %s\n", ((result = hdfsCopy(fs, srcPath, fs, copyPath)) ? "Failed!" : "Success!"));
        totalResult += result;
        fprintf(stderr, "hdfsMove: %s\n", ((result = hdfsMove(fs, copyPath, fs, movePath)) ? "Failed!" : "Success!"));
        totalResult += result;
        
        fprintf(stderr, "hdfsGetDefaultBlockSize: %lld\n", hdfsGetDefaultBlockSize(fs));
        
        fprintf(stderr, "hdfsRename: %s\n", ((result = hdfsRename(fs, srcPath, dstPath)) ? "Failed!" : "Success!"));
        totalResult += result;
        fprintf(stderr, "hdfsRename back: %s\n", ((result = hdfsRename(fs, dstPath, srcPath)) ? "Failed!" : "Success!"));
        totalResult += result;
        
        const char* slashTmp = "/tmp";
        const char* newDirectory = "/tmp/newdir";
        fprintf(stderr, "hdfsCreateDirectory: %s\n", ((result = hdfsCreateDirectory(fs, newDirectory)) ? "Failed!" : "Success!"));
        totalResult += result;
        
        fprintf(stderr, "hdfsSetReplication: %s\n", ((result = hdfsSetReplication(fs, srcPath, 1)) ? "Failed!" : "Success!"));
        totalResult += result;
        
        char buffer[256];
        const char *resp;
        fprintf(stderr, "hdfsGetWorkingDirectory: %s\n", ((resp = hdfsGetWorkingDirectory(fs, buffer, sizeof(buffer))) ? buffer : "Failed!"));
        totalResult += (resp ? 0 : 1);
        fprintf(stderr, "hdfsSetWorkingDirectory: %s\n", ((result = hdfsSetWorkingDirectory(fs, slashTmp)) ? "Failed!" : "Success!"));
        totalResult += result;
        fprintf(stderr, "hdfsGetWorkingDirectory: %s\n", ((resp = hdfsGetWorkingDirectory(fs, buffer, sizeof(buffer))) ? buffer : "Failed!"));
        totalResult += (resp ? 0 : 1);
        
        hdfsFileInfo *fileInfo = NULL;
        if((fileInfo = hdfsGetPathInfo(fs, slashTmp)) != NULL) {
            fprintf(stderr, "hdfsGetPathInfo - SUCCESS!\n");
            fprintf(stderr, "Name: %s, ", fileInfo->mName);
            fprintf(stderr, "Type: %c, ", (char)(fileInfo->mKind));
            fprintf(stderr, "Replication: %d, ", fileInfo->mReplication);
            fprintf(stderr, "BlockSize: %lld, ", fileInfo->mBlockSize);
            fprintf(stderr, "Size: %lld, ", fileInfo->mSize);
            fprintf(stderr, "LastMod: %s", ctime(&fileInfo->mLastMod));
            fprintf(stderr, "Owner: %s, ", fileInfo->mOwner);
            fprintf(stderr, "Group: %s, ", fileInfo->mGroup);
            char permissions[10];
            permission_disp(fileInfo->mPermissions, permissions);
            fprintf(stderr, "Permissions: %d (%s)\n", fileInfo->mPermissions, permissions);
            hdfsFreeFileInfo(fileInfo, 1);
        } else {
            totalResult++;
            fprintf(stderr, "waah! hdfsGetPathInfo for %s - FAILED!\n", slashTmp);
        }
        
        hdfsFileInfo *fileList = 0;
        int numEntries = 0;
        if((fileList = hdfsListDirectory(fs, slashTmp, &numEntries)) != NULL) {
            int i = 0;
            for(i=0; i < numEntries; ++i) {
                fprintf(stderr, "Name: %s, ", fileList[i].mName);
                fprintf(stderr, "Type: %c, ", (char)fileList[i].mKind);
                fprintf(stderr, "Replication: %d, ", fileList[i].mReplication);
                fprintf(stderr, "BlockSize: %lld, ", fileList[i].mBlockSize);
                fprintf(stderr, "Size: %lld, ", fileList[i].mSize);
                fprintf(stderr, "LastMod: %s", ctime(&fileList[i].mLastMod));
                fprintf(stderr, "Owner: %s, ", fileList[i].mOwner);
                fprintf(stderr, "Group: %s, ", fileList[i].mGroup);
                char permissions[10];
                permission_disp(fileList[i].mPermissions, permissions);
                fprintf(stderr, "Permissions: %d (%s)\n", fileList[i].mPermissions, permissions);
            }
            hdfsFreeFileInfo(fileList, numEntries);
        } else {
            if (errno) {
                totalResult++;
                fprintf(stderr, "waah! hdfsListDirectory - FAILED!\n");
            } else {
                fprintf(stderr, "Empty directory!\n");
            }
        }
        
        //        char*** hosts = hdfsGetHosts(fs, srcPath, 0, 1);
        //        if(hosts) {
        //            fprintf(stderr, "hdfsGetHosts - SUCCESS! ... \n");
        //            int i=0;
        //            while(hosts[i]) {
        //                int j = 0;
        //                while(hosts[i][j]) {
        //                    fprintf(stderr,
        //                            "\thosts[%d][%d] - %s\n", i, j, hosts[i][j]);
        //                    ++j;
        //                }
        //                ++i;
        //            }
        //        } else {
        //            totalResult++;
        //            fprintf(stderr, "waah! hdfsGetHosts - FAILED!\n");
        //        }
        
        char *newOwner = "root";
        // setting tmp dir to 777 so later when connectAsUser nobody, we can write to it
        short newPerm = 0666;
        
        // chown write
        fprintf(stderr, "hdfsChown: %s\n", ((result = hdfsChown(fs, writePath, NULL, "users")) ? "Failed!" : "Success!"));
        totalResult += result;
        fprintf(stderr, "hdfsChown: %s\n", ((result = hdfsChown(fs, writePath, newOwner, NULL)) ? "Failed!" : "Success!"));
        totalResult += result;
        // chmod write
        fprintf(stderr, "hdfsChmod: %s\n", ((result = hdfsChmod(fs, writePath, newPerm)) ? "Failed!" : "Success!"));
        totalResult += result;
        
        
        
        sleep(2);
        tTime newMtime = time(NULL);
        tTime newAtime = time(NULL);
        
        // utime write
        fprintf(stderr, "hdfsUtime: %s\n", ((result = hdfsUtime(fs, writePath, newMtime, newAtime)) ? "Failed!" : "Success!"));
        
        totalResult += result;
        
        // chown/chmod/utime read
        hdfsFileInfo *finfo = hdfsGetPathInfo(fs, writePath);
        
        fprintf(stderr, "hdfsChown read: %s\n", ((result = (strcmp(finfo->mOwner, newOwner) != 0)) ? "Failed!" : "Success!"));
        totalResult += result;
        
        fprintf(stderr, "hdfsChmod read: %s\n", ((result = (finfo->mPermissions != newPerm)) ? "Failed!" : "Success!"));
        totalResult += result;
        
        // will later use /tmp/ as a different user so enable it
        fprintf(stderr, "hdfsChmod: %s\n", ((result = hdfsChmod(fs, "/tmp/", 0777)) ? "Failed!" : "Success!"));
        totalResult += result;
        
        fprintf(stderr,"newMTime=%ld\n",newMtime);
        fprintf(stderr,"curMTime=%ld\n",finfo->mLastMod);
        
        
        fprintf(stderr, "hdfsUtime read (mtime): %s\n", ((result = (finfo->mLastMod != newMtime / 1000)) ? "Failed!" : "Success!"));
        totalResult += result;
        
        hdfsFreeFileInfo(finfo, 1);
        
        // Clean up
        fprintf(stderr, "hdfsDelete: %s\n", ((result = hdfsDelete(fs, newDirectory, 1)) ? "Failed!" : "Success!"));
        totalResult += result;
        fprintf(stderr, "hdfsDelete: %s\n", ((result = hdfsDelete(fs, srcPath, 1)) ? "Failed!" : "Success!"));
        totalResult += result;
//        fprintf(stderr, "hdfsDelete: %s\n", ((result = hdfsDelete(fs, movePath, 1)) ? "Failed!" : "Success!"));
//        totalResult += result;
        fprintf(stderr, "hdfsExists: %s\n", ((result = hdfsExists(fs, newDirectory)) ? "Success!" : "Failed!"));
        totalResult += (result ? 0 : 1);
    }
    
    {
        // TEST APPENDS
        const char *writePath = "/tmp/appends";
        
        // CREATE
        hdfsFile writeFile = hdfsOpenFile(fs, writePath, O_WRONLY, 0, 0, 0);
        if(!writeFile) {
            fprintf(stderr, "Failed to open %s for writing!\n", writePath);
            exit(-1);
        }
        fprintf(stderr, "Opened %s for writing successfully...\n", writePath);
        
        const char* buffer = "Hello,";
        tSize num_written_bytes = hdfsWrite(fs, writeFile, (void*)buffer, strlen(buffer));
        fprintf(stderr, "Wrote %d bytes\n", num_written_bytes);
        
        if (hdfsFlush(fs, writeFile)) {
            fprintf(stderr, "Failed to 'flush' %s\n", writePath);
            exit(-1);
        }
        fprintf(stderr, "Flushed %s successfully!\n", writePath);
        
        hdfsCloseFile(fs, writeFile);
        
        fprintf(stderr, "hdfsSetReplication: %s\n", ((result = hdfsSetReplication(fs, writePath, 1)) ? "Failed!" : "Success!"));
        totalResult += result;
        
        // RE-OPEN
        writeFile = hdfsOpenFile(fs, writePath, O_WRONLY|O_APPEND, 0, 0, 0);
        if(!writeFile) {
            fprintf(stderr, "Failed to open %s for writing!\n", writePath);
            exit(-1);
        }
        fprintf(stderr, "Opened %s for appending successfully...\n", writePath);
        
        buffer = " World";
        num_written_bytes = hdfsWrite(fs, writeFile, (void*)buffer, strlen(buffer) + 1);
        fprintf(stderr, "Wrote %d bytes\n", num_written_bytes);
        
        if (hdfsFlush(fs, writeFile)) {
            fprintf(stderr, "Failed to 'flush' %s\n", writePath);
            exit(-1);
        }
        fprintf(stderr, "Flushed %s successfully!\n", writePath);
        
        hdfsCloseFile(fs, writeFile);

        // CHECK size
        hdfsFileInfo *finfo = hdfsGetPathInfo(fs, writePath);
        fprintf(stderr, "fileinfo->mSize: == total %s\n", ((result = (finfo->mSize == strlen("Hello, World") + 1)) ? "Success!" : "Failed!"));
        totalResult += (result ? 0 : 1);
        
        // READ and check data
        hdfsFile readFile = hdfsOpenFile(fs, writePath, O_RDONLY, 0, 0, 0);
        if (!readFile) {
            fprintf(stderr, "Failed to open %s for reading!\n", writePath);
            exit(-1);
        }
        
        char rdbuffer[32];
        tSize num_read_bytes = hdfsRead(fs, readFile, (void*)rdbuffer, sizeof(rdbuffer));
        fprintf(stderr, "Read following %d bytes:\n%s\n",
                num_read_bytes, rdbuffer);
        
        fprintf(stderr, "read == Hello, World %s\n", (result = (strcmp(rdbuffer, "Hello, World") == 0)) ? "Success!" : "Failed!");
        
        hdfsCloseFile(fs, readFile);
        
        // DONE test appends
    }
    
    
    totalResult += (hdfsDisconnect(fs) != 0);
    
    {
        //
        // Now test as connecting as a specific user
        // This is only meant to test that we connected as that user, not to test
        // the actual fs user capabilities. Thus just create a file and read
        // the owner is correct.
        
        const char *tuser = "nobody";
        const char* writePath = "/tmp/usertestfile.txt";
        
        fs = hdfsConnectAsUserNewInstance("default", 50070, tuser);
        if(!fs) {
            fprintf(stderr, "Oops! Failed to connect to hdfs as user %s!\n",tuser);
            exit(-1);
        }
        
        hdfsFile writeFile = hdfsOpenFile(fs, writePath, O_WRONLY|O_CREAT, 0, 0, 0);
        if(!writeFile) {
            fprintf(stderr, "Failed to open %s for writing!\n", writePath);
            exit(-1);
        }
        fprintf(stderr, "Opened %s for writing successfully...\n", writePath);
        
        char* buffer = "Hello, World!";
        tSize num_written_bytes = hdfsWrite(fs, writeFile, (void*)buffer, strlen(buffer)+1);
        fprintf(stderr, "Wrote %d bytes\n", num_written_bytes);
        
        if (hdfsFlush(fs, writeFile)) {
            fprintf(stderr, "Failed to 'flush' %s\n", writePath);
            exit(-1);
        }
        fprintf(stderr, "Flushed %s successfully!\n", writePath);
        
        hdfsCloseFile(fs, writeFile);
        
        hdfsFileInfo *finfo = hdfsGetPathInfo(fs, writePath);
        if (finfo) {
            fprintf(stderr, "hdfs new file user is correct: %s\n", ((result = (strcmp(finfo->mOwner, tuser) != 0)) ? "Failed!" : "Success!"));
        } else {
            fprintf(stderr, "hdfsFileInfo returned by hdfsGetPathInfo is NULL\n");
            result = -1;
        }
        totalResult += result;
    }
    
    totalResult += (hdfsDisconnect(fs) != 0);
    fprintf(stderr, "totalResult == %d\n", totalResult);
    
    if (totalResult != 0) {
        return -1;
    } else {
        return 0;
    }
}

/**
 * vim: ts=4: sw=4: et:
 */
